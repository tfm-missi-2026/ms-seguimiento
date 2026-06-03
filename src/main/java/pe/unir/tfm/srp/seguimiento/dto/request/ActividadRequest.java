package pe.unir.tfm.srp.seguimiento.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ActividadRequest(
    @NotNull UUID tipoActividadId,
    @NotNull UUID modalidadId,
    @NotBlank @Size(max = 200) String titulo,
    String descripcion,
    @NotNull LocalDate fecha,
    @NotNull LocalTime horaInicio,
    @NotNull LocalTime horaFin,
    @NotNull UUID organizadorId
) {}
