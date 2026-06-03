package pe.unir.tfm.srp.seguimiento.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EliminacionRequest(
    @NotBlank @Size(max = 500) String motivoEliminacion
) {}
