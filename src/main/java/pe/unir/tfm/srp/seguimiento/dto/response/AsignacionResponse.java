package pe.unir.tfm.srp.seguimiento.dto.response;

import java.util.UUID;

public record AsignacionResponse(
    UUID id,
    UUID tareaId,
    UUID usuarioId,
    Short estado
) {}
