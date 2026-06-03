package pe.unir.tfm.srp.seguimiento.dto.response;

import java.util.UUID;

public record LbAsignacionResponse(
    UUID id,
    UUID lineaBaseId,
    UUID asignacionId,
    UUID tareaId,
    UUID usuarioId
) {}
