package pe.unir.tfm.srp.seguimiento.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.seguimiento.dto.response.AvanceProyectoResponse;
import pe.unir.tfm.srp.seguimiento.dto.response.AvanceTareaResponse;
import pe.unir.tfm.srp.seguimiento.exception.RecursoNoEncontradoException;
import pe.unir.tfm.srp.seguimiento.model.AvanceTareaAgregado;
import pe.unir.tfm.srp.seguimiento.model.LineaBase;
import pe.unir.tfm.srp.seguimiento.repository.AvanceMapper;
import pe.unir.tfm.srp.seguimiento.repository.LineaBaseMapper;

@Service
@RequiredArgsConstructor
public class AvanceService {

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);

    private final AvanceMapper avanceMapper;
    private final LineaBaseMapper lineaBaseMapper;

    public AvanceProyectoResponse avancePorProyecto(UUID proyectoId) {
        LineaBase vigente = lineaBaseVigente(proyectoId);

        List<AvanceTareaResponse> tareas = avanceMapper.avancePorLineaBase(vigente.getId()).stream()
                .map(this::aTareaResponse)
                .toList();

        BigDecimal estimadasTotal = tareas.stream()
                .map(AvanceTareaResponse::horasEstimadas)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal registradasTotal = tareas.stream()
                .map(AvanceTareaResponse::horasRegistradas)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AvanceProyectoResponse(
                proyectoId,
                vigente.getId(),
                vigente.getVersion(),
                estimadasTotal,
                registradasTotal,
                porcentaje(registradasTotal, estimadasTotal),
                tareas);
    }

    private LineaBase lineaBaseVigente(UUID proyectoId) {
        List<LineaBase> lineasBase = lineaBaseMapper.listarPorProyecto(proyectoId);
        if (lineasBase.isEmpty()) {
            throw new RecursoNoEncontradoException(
                    "No hay linea base congelada para el proyecto " + proyectoId);
        }
        return lineasBase.get(0);
    }

    private AvanceTareaResponse aTareaResponse(AvanceTareaAgregado agregado) {
        BigDecimal estimadas = agregado.getHorasEstimadas() != null
                ? BigDecimal.valueOf(agregado.getHorasEstimadas()) : BigDecimal.ZERO;
        BigDecimal registradas = agregado.getHorasRegistradas() != null
                ? agregado.getHorasRegistradas() : BigDecimal.ZERO;
        return new AvanceTareaResponse(
                agregado.getTareaId(),
                agregado.getNombre(),
                estimadas,
                registradas,
                porcentaje(registradas, estimadas),
                registradas.subtract(estimadas));
    }

    private BigDecimal porcentaje(BigDecimal registradas, BigDecimal estimadas) {
        if (estimadas.signum() <= 0) {
            return BigDecimal.ZERO;
        }
        return registradas.multiply(CIEN).divide(estimadas, 2, RoundingMode.HALF_UP);
    }
}
