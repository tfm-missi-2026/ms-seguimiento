package pe.unir.tfm.srp.seguimiento.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record AsignacionRequest(
    @NotNull UUID tareaId,
    @NotNull UUID usuarioId,
    @PositiveOrZero BigDecimal horasPlanificadas,
    LocalDate fechaInicioPlanificada,
    LocalDate fechaFinPlanificada,
    boolean confirmarSobrecarga
) {}
