package pe.unir.tfm.srp.seguimiento.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AsignacionResponse(
    UUID id,
    UUID tareaId,
    UUID usuarioId,
    BigDecimal horasPlanificadas,
    LocalDate fechaInicioPlanificada,
    LocalDate fechaFinPlanificada,
    Short estado
) {}
