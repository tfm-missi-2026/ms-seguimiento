package pe.unir.tfm.srp.seguimiento.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pe.unir.tfm.srp.seguimiento.dto.response.CargaRecursoResponse;
import pe.unir.tfm.srp.seguimiento.exception.ConflictoNegocioException;
import pe.unir.tfm.srp.seguimiento.model.CargaAgregado;
import pe.unir.tfm.srp.seguimiento.repository.CargaMapper;

@ExtendWith(MockitoExtension.class)
class CargaServiceTest {

    private static final UUID USUARIO = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final LocalDate DESDE = LocalDate.of(2026, 6, 1);
    private static final LocalDate HASTA = LocalDate.of(2026, 6, 30);

    @Mock
    private CargaMapper cargaMapper;

    @InjectMocks
    private CargaService cargaService;

    @Test
    void recursoSinAsignaciones_devuelveCargaCeroSinSobrecarga() {
        when(cargaMapper.agregarCargaPorUsuario(USUARIO, DESDE, HASTA, null))
                .thenReturn(agregado(BigDecimal.ZERO, BigDecimal.ZERO, 0));

        CargaRecursoResponse carga = cargaService.cargaPorUsuario(USUARIO, DESDE, HASTA, null);

        assertThat(carga.usuarioId()).isEqualTo(USUARIO);
        assertThat(carga.horasPlanificadas()).isEqualByComparingTo("0");
        assertThat(carga.horasRegistradas()).isEqualByComparingTo("0");
        assertThat(carga.numeroTareasActivas()).isZero();
        assertThat(carga.porcentajeUtilizacion()).isEqualByComparingTo("0");
        assertThat(carga.sobrecarga()).isFalse();
    }

    @Test
    void recursoConCargaNormal_calculaPorcentajeYNoMarcaSobrecarga() {
        when(cargaMapper.agregarCargaPorUsuario(USUARIO, DESDE, HASTA, null))
                .thenReturn(agregado(new BigDecimal("40.00"), new BigDecimal("30.00"), 3));

        CargaRecursoResponse carga = cargaService.cargaPorUsuario(USUARIO, DESDE, HASTA, null);

        assertThat(carga.horasPlanificadas()).isEqualByComparingTo("40.00");
        assertThat(carga.horasRegistradas()).isEqualByComparingTo("30.00");
        assertThat(carga.numeroTareasActivas()).isEqualTo(3);
        assertThat(carga.porcentajeUtilizacion()).isEqualByComparingTo("75.00");
        assertThat(carga.sobrecarga()).isFalse();
    }

    @Test
    void recursoEnSobrecarga_marcaSobrecargaYPorcentajeMayorACien() {
        when(cargaMapper.agregarCargaPorUsuario(USUARIO, DESDE, HASTA, null))
                .thenReturn(agregado(new BigDecimal("40.00"), new BigDecimal("50.00"), 4));

        CargaRecursoResponse carga = cargaService.cargaPorUsuario(USUARIO, DESDE, HASTA, null);

        assertThat(carga.porcentajeUtilizacion()).isEqualByComparingTo("125.00");
        assertThat(carga.sobrecarga()).isTrue();
    }

    @Test
    void rangoInvalido_lanzaConflictoNegocioYNoConsultaElMapper() {
        assertThatThrownBy(() -> cargaService.cargaPorUsuario(USUARIO, HASTA, DESDE, null))
                .isInstanceOf(ConflictoNegocioException.class);

        verifyNoInteractions(cargaMapper);
    }

    @Test
    void rangoInvalidoEnEquipo_lanzaConflictoNegocio() {
        assertThatThrownBy(() -> cargaService.cargaEquipo(HASTA, DESDE, null))
                .isInstanceOf(ConflictoNegocioException.class);

        verifyNoInteractions(cargaMapper);
    }

    private CargaAgregado agregado(BigDecimal planificadas, BigDecimal registradas, long numeroTareas) {
        return CargaAgregado.builder()
                .horasPlanificadas(planificadas)
                .horasRegistradas(registradas)
                .numeroTareasActivas(numeroTareas)
                .build();
    }
}
