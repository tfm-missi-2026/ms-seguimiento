package pe.unir.tfm.srp.seguimiento.dto.request;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record AsignacionRequest(
    @NotNull UUID tareaId,
    @NotNull UUID usuarioId,
    @NotNull LocalDate fechaInicioPlanificada,
    boolean confirmarSobrecarga
) {}
