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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import pe.unir.tfm.srp.seguimiento.client.ProyectosClient;
import pe.unir.tfm.srp.seguimiento.client.TareaFechasRequest;
import pe.unir.tfm.srp.seguimiento.client.TareaProyectoResponse;
import pe.unir.tfm.srp.seguimiento.client.TareaSituacionRequest;
import pe.unir.tfm.srp.seguimiento.config.CurrentUserResolver;
import pe.unir.tfm.srp.seguimiento.dto.conversor.AsignacionConversor;
import pe.unir.tfm.srp.seguimiento.dto.request.AsignacionRequest;
import pe.unir.tfm.srp.seguimiento.dto.request.AsignacionSituacionRequest;
import pe.unir.tfm.srp.seguimiento.dto.request.EliminacionRequest;
import pe.unir.tfm.srp.seguimiento.exception.ConflictoNegocioException;
import pe.unir.tfm.srp.seguimiento.model.Asignacion;
import pe.unir.tfm.srp.seguimiento.repository.AsignacionMapper;
import pe.unir.tfm.srp.seguimiento.repository.LineaBaseMapper;

@ExtendWith(MockitoExtension.class)
class AsignacionServiceTest {

    private static final UUID ASIGNACION = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID TAREA = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID USUARIO = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID SITUACION = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final LocalDate LUNES =LocalDate.of(2026, 6, 1);
    private static final LocalDate VIERNES = LocalDate.of(2026, 6, 5);
    private static final LocalDate SABADO = LocalDate.of(2026, 6, 6);
    private static final LocalDate LUNES_SIGUIENTE = LocalDate.of(2026, 6, 8);

    @Mock
    private AsignacionMapper asignacionMapper;

    @Mock
    private LineaBaseMapper lineaBaseMapper;

    @Mock
    private ProyectosClient proyectosClient;

    @Mock
    private AsignacionConversor asignacionConversor;

    @Mock
    private CurrentUserResolver currentUserResolver;

    @InjectMocks
    private AsignacionService asignacionService;

    private AsignacionRequest request(LocalDate inicio) {
        return new AsignacionRequest(TAREA, USUARIO, inicio);
    }

    private void tareaCon(int dias, int horas) {
        when(proyectosClient.obtenerTarea(TAREA))
                .thenReturn(new TareaProyectoResponse(TAREA, (short) dias, (short) horas));
    }

    @Test
    void crear_conTareaYaAsignada_lanzaConflictoYNoInserta() {
        when(asignacionMapper.contarPorTarea(TAREA)).thenReturn(1);

        assertThatThrownBy(() -> asignacionService.crear(request(LUNES)))
                .isInstanceOf(ConflictoNegocioException.class)
                .hasMessage("La tarea ya esta asignada a un recurso");

        verify(asignacionMapper, never()).insertar(any());
    }

    @Test
    void crear_conInicioEnFinDeSemana_lanzaConflictoYNoInserta() {
        assertThatThrownBy(() -> asignacionService.crear(request(SABADO)))
                .isInstanceOf(ConflictoNegocioException.class);

        verify(asignacionMapper, never()).insertar(any());
    }

    @Test
    void crear_calculaFinSaltandoFinDeSemanaYTomaHorasDeLaTarea() {
        tareaCon(2, 12);
        when(asignacionMapper.sumarHorasPlanificadasEnPeriodo(eq(USUARIO), eq(VIERNES), eq(LUNES_SIGUIENTE)))
                .thenReturn(BigDecimal.ZERO);

        asignacionService.crear(request(VIERNES));

        ArgumentCaptor<Asignacion> guardada = ArgumentCaptor.forClass(Asignacion.class);
        verify(asignacionMapper).insertar(guardada.capture());
        assertThat(guardada.getValue().getFechaFinPlanificada()).isEqualTo(LUNES_SIGUIENTE);
        assertThat(guardada.getValue().getHorasPlanificadas()).isEqualByComparingTo("12");
    }

    @Test
    void crear_actualizaFechasDeLaTarea() {
        tareaCon(1, 8);
        when(asignacionMapper.sumarHorasPlanificadasEnPeriodo(eq(USUARIO), eq(LUNES), eq(LUNES)))
                .thenReturn(BigDecimal.ZERO);

        asignacionService.crear(request(LUNES));

        verify(proyectosClient).actualizarFechas(TAREA, new TareaFechasRequest(LUNES, LUNES));
    }

    @Test
    void crear_queExcedeCapacidad_lanzaConflictoYNoInserta() {
        tareaCon(1, 8);
        when(asignacionMapper.sumarHorasPlanificadasEnPeriodo(eq(USUARIO), eq(LUNES), eq(LUNES)))
                .thenReturn(new BigDecimal("8"));

        assertThatThrownBy(() -> asignacionService.crear(request(LUNES)))
                .isInstanceOf(ConflictoNegocioException.class);

        verify(asignacionMapper, never()).insertar(any());
        verify(proyectosClient, never()).actualizarFechas(any(), any());
    }

    @Test
    void eliminar_conTareaCongelada_lanzaConflictoYNoElimina() {
        when(asignacionMapper.buscarPorId(ASIGNACION)).thenReturn(asignacionDeLaTarea());
        when(lineaBaseMapper.contarTareaCongelada(TAREA)).thenReturn(1);

        assertThatThrownBy(() -> asignacionService.eliminar(ASIGNACION, new EliminacionRequest("Motivo")))
                .isInstanceOf(ConflictoNegocioException.class)
                .hasMessage("La asignacion pertenece a una linea base congelada");

        verify(asignacionMapper, never()).eliminarLogico(any(), any(), any());
    }

    @Test
    void eliminar_conTareaSinCongelar_elimina() {
        when(asignacionMapper.buscarPorId(ASIGNACION)).thenReturn(asignacionDeLaTarea());
        when(lineaBaseMapper.contarTareaCongelada(TAREA)).thenReturn(0);
        when(currentUserResolver.obtenerUsuarioActualId()).thenReturn(USUARIO);

        asignacionService.eliminar(ASIGNACION, new EliminacionRequest("Motivo"));

        verify(asignacionMapper).eliminarLogico(ASIGNACION, USUARIO, "Motivo");
    }

    @Test
    void actualizarSituacion_delRecursoAsignado_llamaAProyectos() {
        when(asignacionMapper.buscarPorId(ASIGNACION)).thenReturn(asignacionDeLaTarea());
        when(currentUserResolver.obtenerUsuarioActualId()).thenReturn(USUARIO);

        asignacionService.actualizarSituacion(ASIGNACION, new AsignacionSituacionRequest(SITUACION));

        verify(proyectosClient).actualizarSituacion(TAREA, new TareaSituacionRequest(SITUACION));
    }

    @Test
    void actualizarSituacion_deOtroUsuario_lanzaAccesoDenegadoYNoLlama() {
        when(asignacionMapper.buscarPorId(ASIGNACION)).thenReturn(asignacionDeLaTarea());
        when(currentUserResolver.obtenerUsuarioActualId()).thenReturn(SITUACION);

        assertThatThrownBy(() -> asignacionService.actualizarSituacion(ASIGNACION, new AsignacionSituacionRequest(SITUACION)))
                .isInstanceOf(AccessDeniedException.class);

        verify(proyectosClient, never()).actualizarSituacion(any(), any());
    }

    private Asignacion asignacionDeLaTarea() {
        return Asignacion.builder().id(ASIGNACION).tareaId(TAREA).usuarioId(USUARIO).build();
    }
}
