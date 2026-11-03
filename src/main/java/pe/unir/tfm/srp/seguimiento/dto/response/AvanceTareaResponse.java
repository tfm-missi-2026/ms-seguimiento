package pe.unir.tfm.srp.seguimiento.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AvanceTareaResponse(
    UUID tareaId,
    String nombre,
    LocalDate fechaInicioPlanificada,
    LocalDate fechaFinPlanificada,
    LocalDate primeraFechaRegistrada,
    LocalDate ultimaFechaRegistrada,
    BigDecimal horasEstimadas,
    BigDecimal horasRegistradas,
    BigDecimal porcentajeAvance,
    BigDecimal desviacionHoras
) {}
