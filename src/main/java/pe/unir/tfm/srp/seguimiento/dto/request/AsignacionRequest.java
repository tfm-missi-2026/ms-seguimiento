package pe.unir.tfm.srp.seguimiento.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record AsignacionRequest(
    @NotNull UUID tareaId,
    @NotNull UUID usuarioId
) {}
