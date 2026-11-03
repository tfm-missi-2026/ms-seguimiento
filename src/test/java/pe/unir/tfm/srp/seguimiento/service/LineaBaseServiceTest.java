package pe.unir.tfm.srp.seguimiento.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pe.unir.tfm.srp.seguimiento.config.CurrentUserResolver;
import pe.unir.tfm.srp.seguimiento.dto.conversor.LineaBaseConversor;
import pe.unir.tfm.srp.seguimiento.dto.request.CongelarLineaBaseRequest;
import pe.unir.tfm.srp.seguimiento.exception.ConflictoNegocioException;
import pe.unir.tfm.srp.seguimiento.model.LbTarea;
import pe.unir.tfm.srp.seguimiento.model.LineaBase;
import pe.unir.tfm.srp.seguimiento.repository.LineaBaseMapper;

@ExtendWith(MockitoExtension.class)
class LineaBaseServiceTest {

    private static final UUID PROYECTO = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID TAREA = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Mock
    private LineaBaseMapper lineaBaseMapper;

    @Mock
    private LineaBaseConversor lineaBaseConversor;

    @Mock
    private CurrentUserResolver currentUserResolver;

    @InjectMocks
    private LineaBaseService lineaBaseService;

    private CongelarLineaBaseRequest request() {
        return new CongelarLineaBaseRequest(PROYECTO, "x", List.of(new CongelarLineaBaseRequest.TareaSnapshot(
                TAREA, "Tarea", null, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 1), (short) 8)), List.of());
    }

    @Test
    void congelar_conTareaYaCongelada_lanzaConflictoYNoInserta() {
        when(lineaBaseMapper.contarTareaCongelada(TAREA)).thenReturn(1);

        assertThatThrownBy(() -> lineaBaseService.congelar(request()))
                .isInstanceOf(ConflictoNegocioException.class)
                .hasMessage("La tarea ya esta congelada en la linea base");

        verify(lineaBaseMapper, never()).insertarTarea(any());
    }

    @Test
    void congelar_subproyectoNuevo_agregaALaLineaBaseExistente() {
        UUID existente = UUID.randomUUID();
        when(lineaBaseMapper.contarTareaCongelada(TAREA)).thenReturn(0);
        when(lineaBaseMapper.listarPorProyecto(PROYECTO))
                .thenReturn(List.of(LineaBase.builder().id(existente).proyectoId(PROYECTO).build()));
        lenient().when(lineaBaseMapper.buscarPorId(existente))
                .thenReturn(LineaBase.builder().id(existente).proyectoId(PROYECTO).build());

        lineaBaseService.congelar(request());

        verify(lineaBaseMapper, never()).insertarCabecera(any());
        verify(lineaBaseMapper).insertarTarea(any(LbTarea.class));
    }

    @Test
    void congelar_sinTareas_lanzaConflicto() {
        CongelarLineaBaseRequest vacio = new CongelarLineaBaseRequest(PROYECTO, "x", List.of(), List.of());

        assertThatThrownBy(() -> lineaBaseService.congelar(vacio))
                .isInstanceOf(ConflictoNegocioException.class);
    }
}
