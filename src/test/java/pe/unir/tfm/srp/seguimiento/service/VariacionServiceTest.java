package pe.unir.tfm.srp.seguimiento.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pe.unir.tfm.srp.seguimiento.config.CurrentUserResolver;
import pe.unir.tfm.srp.seguimiento.dto.conversor.VariacionConversor;
import pe.unir.tfm.srp.seguimiento.dto.request.EliminacionRequest;
import pe.unir.tfm.srp.seguimiento.dto.request.VariacionCrearRequest;
import pe.unir.tfm.srp.seguimiento.exception.ConflictoNegocioException;
import pe.unir.tfm.srp.seguimiento.model.Variacion;
import pe.unir.tfm.srp.seguimiento.repository.LineaBaseMapper;
import pe.unir.tfm.srp.seguimiento.repository.VariacionMapper;

@ExtendWith(MockitoExtension.class)
class VariacionServiceTest {

    private static final UUID TAREA = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Mock
    private VariacionMapper variacionMapper;

    @Mock
    private LineaBaseMapper lineaBaseMapper;

    @Mock
    private VariacionConversor variacionConversor;

    @Mock
    private CurrentUserResolver currentUserResolver;

    @InjectMocks
    private VariacionService variacionService;

    private VariacionCrearRequest request() {
        return new VariacionCrearRequest(TAREA, UUID.randomUUID(), "d", "j", null, null,
                LocalDate.of(2026, 6, 1), UUID.randomUUID());
    }

    @Test
    void crear_conTareaNoCongelada_lanzaConflictoYNoInserta() {
        when(lineaBaseMapper.contarTareaCongelada(TAREA)).thenReturn(0);

        assertThatThrownBy(() -> variacionService.crear(request()))
                .isInstanceOf(ConflictoNegocioException.class)
                .hasMessage("La tarea no pertenece a una linea base congelada");

        verify(variacionMapper, never()).insertar(any());
    }

    @Test
    void crear_conTareaCongelada_inserta() {
        when(lineaBaseMapper.contarTareaCongelada(TAREA)).thenReturn(1);

        variacionService.crear(request());

        verify(variacionMapper).insertar(any());
    }

    @Test
    void eliminar_conVariacionResuelta_lanzaConflictoYNoElimina() {
        UUID id = UUID.randomUUID();
        when(variacionMapper.buscarPorId(id))
                .thenReturn(Variacion.builder().id(id).resueltoPor(UUID.randomUUID()).build());

        assertThatThrownBy(() -> variacionService.eliminar(id, new EliminacionRequest("Motivo")))
                .isInstanceOf(ConflictoNegocioException.class)
                .hasMessage("Solo se pueden eliminar variaciones pendientes");

        verify(variacionMapper, never()).eliminarLogico(any(), any(), any());
    }

    @Test
    void eliminar_conVariacionPendiente_elimina() {
        UUID id = UUID.randomUUID();
        UUID usuario = UUID.randomUUID();
        when(variacionMapper.buscarPorId(id)).thenReturn(Variacion.builder().id(id).build());
        when(currentUserResolver.obtenerUsuarioActualId()).thenReturn(usuario);

        variacionService.eliminar(id, new EliminacionRequest("Motivo"));

        verify(variacionMapper).eliminarLogico(id, usuario, "Motivo");
    }
}
