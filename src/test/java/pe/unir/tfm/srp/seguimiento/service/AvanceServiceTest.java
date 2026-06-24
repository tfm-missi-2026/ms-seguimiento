package pe.unir.tfm.srp.seguimiento.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pe.unir.tfm.srp.seguimiento.dto.response.AvanceProyectoResponse;
import pe.unir.tfm.srp.seguimiento.exception.RecursoNoEncontradoException;
import pe.unir.tfm.srp.seguimiento.model.AvanceTareaAgregado;
import pe.unir.tfm.srp.seguimiento.model.LineaBase;
import pe.unir.tfm.srp.seguimiento.repository.AvanceMapper;
import pe.unir.tfm.srp.seguimiento.repository.LineaBaseMapper;

@ExtendWith(MockitoExtension.class)
class AvanceServiceTest {

    private static final UUID PROYECTO = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID LINEA_BASE = UUID.fromString("55555555-5555-5555-5555-555555555555");

    @Mock
    private AvanceMapper avanceMapper;

    @Mock
    private LineaBaseMapper lineaBaseMapper;

    @InjectMocks
    private AvanceService avanceService;

    @Test
    void proyectoSinLineaBase_lanzaRecursoNoEncontrado() {
        when(lineaBaseMapper.listarPorProyecto(PROYECTO)).thenReturn(List.of());

        assertThatThrownBy(() -> avanceService.avancePorProyecto(PROYECTO))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void proyectoConLineaBase_calculaAvancePorTareaYTotales() {
        when(lineaBaseMapper.listarPorProyecto(PROYECTO))
                .thenReturn(List.of(LineaBase.builder().id(LINEA_BASE).version((short) 1).build()));
        when(avanceMapper.avancePorLineaBase(LINEA_BASE)).thenReturn(List.of(
                tarea("Tarea A", (short) 100, new BigDecimal("50.00")),
                tarea("Tarea B", (short) 40, new BigDecimal("60.00"))));

        AvanceProyectoResponse avance = avanceService.avancePorProyecto(PROYECTO);

        assertThat(avance.lineaBaseId()).isEqualTo(LINEA_BASE);
        assertThat(avance.version()).isEqualTo((short) 1);
        assertThat(avance.horasEstimadasTotal()).isEqualByComparingTo("140");
        assertThat(avance.horasRegistradasTotal()).isEqualByComparingTo("110.00");
        assertThat(avance.porcentajeAvance()).isEqualByComparingTo("78.57");
        assertThat(avance.tareas()).hasSize(2);

        assertTarea(avance, 0, "50.00", "50.00", "-50.00");
        assertTarea(avance, 1, "150.00", "60.00", "20.00");
    }

    private void assertTarea(AvanceProyectoResponse avance, int idx,
                             String porcentaje, String registradas, String desviacion) {
        var t = avance.tareas().get(idx);
        assertThat(t.porcentajeAvance()).isEqualByComparingTo(porcentaje);
        assertThat(t.horasRegistradas()).isEqualByComparingTo(registradas);
        assertThat(t.desviacionHoras()).isEqualByComparingTo(desviacion);
    }

    private AvanceTareaAgregado tarea(String nombre, Short estimadas, BigDecimal registradas) {
        return AvanceTareaAgregado.builder()
                .tareaId(UUID.randomUUID())
                .nombre(nombre)
                .horasEstimadas(estimadas)
                .horasRegistradas(registradas)
                .build();
    }
}
