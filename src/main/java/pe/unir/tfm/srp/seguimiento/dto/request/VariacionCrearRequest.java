package pe.unir.tfm.srp.seguimiento.dto.request;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VariacionCrearRequest(
    UUID tareaId,
    @NotNull UUID tipoVariacionId,
    @NotBlank String descripcion,
    @NotBlank String justificacion,
    @Size(max = 500) String valorAnterior,
    @Size(max = 500) String valorNuevo,
    @NotNull LocalDate fechaDeteccion,
    @NotNull UUID reportadaPor,
    @NotNull UUID situacionId
) {}
