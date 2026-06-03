package pe.unir.tfm.srp.seguimiento.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VariacionResolverRequest(
    @NotNull UUID situacionId,
    @Size(max = 500) String observacionResolucion
) {}
