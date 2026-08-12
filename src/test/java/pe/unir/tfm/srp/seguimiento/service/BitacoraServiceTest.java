package pe.unir.tfm.srp.seguimiento.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pe.unir.tfm.srp.seguimiento.config.CurrentUserResolver;
import pe.unir.tfm.srp.seguimiento.dto.conversor.BitacoraConversor;
import pe.unir.tfm.srp.seguimiento.dto.request.BitacoraRequest;
import pe.unir.tfm.srp.seguimiento.dto.response.BitacoraResponse;
import pe.unir.tfm.srp.seguimiento.exception.ConflictoNegocioException;
import pe.unir.tfm.srp.seguimiento.exception.RecursoNoEncontradoException;
import pe.unir.tfm.srp.seguimiento.model.Bitacora;
import pe.unir.tfm.srp.seguimiento.repository.BitacoraMapper;

@ExtendWith(MockitoExtension.class)
class BitacoraServiceTest {

    private static final UUID USUARIO = UUID.fromString("00000000-0000-0000-0000-000000000900");
    private static final UUID ASIG = UUID.fromString("00000000-0000-0000-0000-000000000aaa");
    private static final UUID ACT = UUID.fromString("00000000-0000-0000-0000-000000000bbb");
    private static final LocalDate HOY = LocalDate.of(2026, 8, 7);

    @Mock private BitacoraMapper bitacoraMapper;
    @Mock private BitacoraConversor bitacoraConversor;
    @Mock private CurrentUserResolver currentUserResolver;

    @InjectMocks private BitacoraService bitacoraService;

    private BitacoraRequest requestConAsignacion() {
        return new BitacoraRequest(HOY, LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Trabajo en tarea", ASIG, null);
    }

    private BitacoraRequest requestConActividad() {
        return new BitacoraRequest(HOY, LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Reunion", null, ACT);
    }

    private BitacoraRequest requestSinNinguno() {
        return new BitacoraRequest(HOY, LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Error", null, null);
    }

    private BitacoraRequest requestConAmbos() {
        return new BitacoraRequest(HOY, LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Error", ASIG, ACT);
    }

    private BitacoraRequest requestHorasInvertidas() {
        return new BitacoraRequest(HOY, LocalTime.of(10, 0), LocalTime.of(9, 0),
                "Error", ASIG, null);
    }

    @Test
    void crear_ambosAsignacionYActividad_lanzaConflictoXor() {
        assertThatThrownBy(() -> bitacoraService.crear(requestConAmbos()))
                .isInstanceOf(ConflictoNegocioException.class)
                .hasMessageContaining("Exactamente uno");

        verify(bitacoraMapper, never()).insertar(any());
    }

    @Test
    void crear_sinAsignacionNiActividad_lanzaConflictoXor() {
        assertThatThrownBy(() -> bitacoraService.crear(requestSinNinguno()))
                .isInstanceOf(ConflictoNegocioException.class)
                .hasMessageContaining("Exactamente uno");
    }

    @Test
    void crear_horaFinAnteriorOIgualAInicio_lanzaConflicto() {
        assertThatThrownBy(() -> bitacoraService.crear(requestHorasInvertidas()))
                .isInstanceOf(ConflictoNegocioException.class)
                .hasMessageContaining("hora_fin");

        verify(bitacoraMapper, never()).insertar(any());
    }

    @Test
    void crear_conAsignacionValida_insertaConUsuarioActual() {
        when(currentUserResolver.obtenerUsuarioActualId()).thenReturn(USUARIO);
        when(bitacoraConversor.aBitacoraResponse(any(Bitacora.class)))
                .thenReturn(new BitacoraResponse(UUID.randomUUID(), USUARIO, HOY,
                        LocalTime.of(9, 0), LocalTime.of(10, 0), "Trabajo en tarea", ASIG, null, (short) 1));

        BitacoraResponse resp = bitacoraService.crear(requestConAsignacion());

        assertThat(resp.asignacionId()).isEqualTo(ASIG);
        assertThat(resp.actividadId()).isNull();
        verify(bitacoraMapper).insertar(any(Bitacora.class));
    }

    @Test
    void crear_conActividadValida_inserta() {
        when(currentUserResolver.obtenerUsuarioActualId()).thenReturn(USUARIO);
        when(bitacoraConversor.aBitacoraResponse(any(Bitacora.class)))
                .thenReturn(new BitacoraResponse(UUID.randomUUID(), USUARIO, HOY,
                        LocalTime.of(9, 0), LocalTime.of(10, 0), "Reunion", null, ACT, (short) 1));

        BitacoraResponse resp = bitacoraService.crear(requestConActividad());

        assertThat(resp.actividadId()).isEqualTo(ACT);
    }

    @Test
    void buscarPorId_noExiste_lanzaRecursoNoEncontrado() {
        when(bitacoraMapper.buscarPorId(any())).thenReturn(null);

        assertThatThrownBy(() -> bitacoraService.buscarPorId(UUID.randomUUID()))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void actualizar_conXorInvalido_lanzaConflicto() {
        UUID id = UUID.randomUUID();
        when(bitacoraMapper.buscarPorId(id)).thenReturn(new Bitacora());

        assertThatThrownBy(() -> bitacoraService.actualizar(id, requestConAmbos()))
                .isInstanceOf(ConflictoNegocioException.class)
                .hasMessageContaining("Exactamente uno");
    }

    @Test
    void listarPorUsuarioYFecha_delegaMappers() {
        when(bitacoraMapper.listarPorUsuarioYFecha(USUARIO, HOY)).thenReturn(List.of());
        when(bitacoraConversor.aBitacoraResponseList(List.of())).thenReturn(List.of());

        List<BitacoraResponse> resultado = bitacoraService.listarPorUsuarioYFecha(USUARIO, HOY);

        assertThat(resultado).isEmpty();
        verify(bitacoraMapper).listarPorUsuarioYFecha(USUARIO, HOY);
    }
}
