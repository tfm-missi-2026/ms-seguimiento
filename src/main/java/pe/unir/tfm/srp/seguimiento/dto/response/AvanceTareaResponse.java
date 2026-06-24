package pe.unir.tfm.srp.seguimiento.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record AvanceTareaResponse(
    UUID tareaId,
    String nombre,
    BigDecimal horasEstimadas,
    BigDecimal horasRegistradas,
    BigDecimal porcentajeAvance,
    BigDecimal desviacionHoras
) {}
