package pe.unir.tfm.srp.seguimiento.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record CargaRecursoResponse(
    UUID usuarioId,
    BigDecimal horasPlanificadas,
    BigDecimal horasRegistradas,
    long numeroTareasActivas,
    BigDecimal porcentajeUtilizacion,
    boolean sobrecarga
) {}
