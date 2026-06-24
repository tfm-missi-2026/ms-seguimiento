package pe.unir.tfm.srp.seguimiento.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pe.unir.tfm.srp.seguimiento.config.CurrentUserResolver;
import pe.unir.tfm.srp.seguimiento.dto.conversor.AsignacionConversor;
import pe.unir.tfm.srp.seguimiento.dto.request.AsignacionRequest;
import pe.unir.tfm.srp.seguimiento.dto.response.AsignacionResponse;
import pe.unir.tfm.srp.seguimiento.exception.ConflictoNegocioException;
import pe.unir.tfm.srp.seguimiento.model.Asignacion;
import pe.unir.tfm.srp.seguimiento.repository.AsignacionMapper;

@ExtendWith(MockitoExtension.class)
class AsignacionServiceTest {

    private static final UUID TAREA = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID USUARIO = UUID.fromString("33333333-3333-3333-3333-333333333333");
    // 7 dias consecutivos = exactamente 5 dias habiles => capacidad determinista de 40 h
    private static final LocalDate INICIO = LocalDate.of(2026, 6, 1);
    private static final LocalDate FIN = LocalDate.of(2026, 6, 7);

    @Mock
    private AsignacionMapper asignacionMapper;

    @Mock
    private AsignacionConversor asignacionConversor;

    @Mock
    private CurrentUserResolver currentUserResolver;

    @InjectMocks
    private AsignacionService asignacionService;

    private AsignacionRequest request(BigDecimal horas, LocalDate inicio, LocalDate fin, boolean confirmar) {
        return new AsignacionRequest(TAREA, USUARIO, horas, inicio, fin, confirmar);
    }

    @Test
    void crear_conFechaFinAnteriorAInicio_lanzaConflictoYNoInserta() {
        when(asignacionMapper.contar(TAREA, USUARIO)).thenReturn(0);

        assertThatThrownBy(() -> asignacionService.crear(
                request(new BigDecimal("40"), FIN, INICIO, false)))
                .isInstanceOf(ConflictoNegocioException.class);

        verify(asignacionMapper, never()).insertar(any());
    }

    @Test
    void crear_conPeriodoValidoDentroDeCapacidad_insertaYDevuelve() {
        when(asignacionMapper.contar(TAREA, USUARIO)).thenReturn(0);
        when(asignacionMapper.sumarHorasPlanificadasEnPeriodo(eq(USUARIO), eq(INICIO), eq(FIN)))
                .thenReturn(BigDecimal.ZERO);
        when(asignacionConversor.aAsignacionResponse(any(Asignacion.class)))
                .thenReturn(new AsignacionResponse(UUID.randomUUID(), TAREA, USUARIO, new BigDecimal("30"),
                        INICIO, FIN, (short) 1));

        AsignacionResponse respuesta = asignacionService.crear(request(new BigDecimal("30"), INICIO, FIN, false));

        assertThat(respuesta.tareaId()).isEqualTo(TAREA);
        verify(asignacionMapper).insertar(any(Asignacion.class));
    }

    @Test
    void crear_queExcedeCapacidadSinConfirmar_lanzaConflictoYNoInserta() {
        when(asignacionMapper.contar(TAREA, USUARIO)).thenReturn(0);
        when(asignacionMapper.sumarHorasPlanificadasEnPeriodo(eq(USUARIO), eq(INICIO), eq(FIN)))
                .thenReturn(BigDecimal.ZERO);

        assertThatThrownBy(() -> asignacionService.crear(request(new BigDecimal("50"), INICIO, FIN, false)))
                .isInstanceOf(ConflictoNegocioException.class);

        verify(asignacionMapper, never()).insertar(any());
    }

    @Test
    void crear_queExcedeCapacidadConfirmando_inserta() {
        when(asignacionMapper.contar(TAREA, USUARIO)).thenReturn(0);
        when(asignacionConversor.aAsignacionResponse(any(Asignacion.class)))
                .thenReturn(new AsignacionResponse(UUID.randomUUID(), TAREA, USUARIO, new BigDecimal("50"),
                        INICIO, FIN, (short) 1));

        AsignacionResponse respuesta = asignacionService.crear(request(new BigDecimal("50"), INICIO, FIN, true));

        assertThat(respuesta).isNotNull();
        verify(asignacionMapper).insertar(any(Asignacion.class));
    }
}
