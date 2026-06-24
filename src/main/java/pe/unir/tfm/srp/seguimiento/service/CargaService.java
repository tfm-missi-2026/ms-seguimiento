package pe.unir.tfm.srp.seguimiento.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.seguimiento.dto.response.CargaRecursoResponse;
import pe.unir.tfm.srp.seguimiento.exception.ConflictoNegocioException;
import pe.unir.tfm.srp.seguimiento.model.CargaAgregado;
import pe.unir.tfm.srp.seguimiento.repository.CargaMapper;

@Service
@RequiredArgsConstructor
public class CargaService {

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);

    private final CargaMapper cargaMapper;

    public List<CargaRecursoResponse> cargaEquipo(LocalDate desde, LocalDate hasta, List<UUID> tareaIds) {
        validarRango(desde, hasta);
        return cargaMapper.agregarCargaEquipo(desde, hasta, tareaIds).stream()
                .map(this::aResponse)
                .toList();
    }

    public CargaRecursoResponse cargaPorUsuario(UUID usuarioId, LocalDate desde, LocalDate hasta, List<UUID> tareaIds) {
        validarRango(desde, hasta);
        CargaAgregado agregado = cargaMapper.agregarCargaPorUsuario(usuarioId, desde, hasta, tareaIds);
        agregado.setUsuarioId(usuarioId);
        return aResponse(agregado);
    }

    private void validarRango(LocalDate desde, LocalDate hasta) {
        if (desde.isAfter(hasta)) {
            throw new ConflictoNegocioException("El parametro desde debe ser anterior o igual a hasta");
        }
    }

    private CargaRecursoResponse aResponse(CargaAgregado agregado) {
        BigDecimal planificadas = agregado.getHorasPlanificadas() != null
                ? agregado.getHorasPlanificadas() : BigDecimal.ZERO;
        BigDecimal registradas = agregado.getHorasRegistradas() != null
                ? agregado.getHorasRegistradas() : BigDecimal.ZERO;

        BigDecimal porcentajeUtilizacion;
        boolean sobrecarga;
        if (planificadas.signum() <= 0) {
            porcentajeUtilizacion = BigDecimal.ZERO;
            sobrecarga = false;
        } else {
            porcentajeUtilizacion = registradas.multiply(CIEN).divide(planificadas, 2, RoundingMode.HALF_UP);
            sobrecarga = registradas.compareTo(planificadas) > 0;
        }

        return new CargaRecursoResponse(
                agregado.getUsuarioId(),
                planificadas,
                registradas,
                agregado.getNumeroTareasActivas(),
                porcentajeUtilizacion,
                sobrecarga);
    }
}
