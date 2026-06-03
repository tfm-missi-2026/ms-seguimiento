package pe.unir.tfm.srp.seguimiento.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BitacoraRequest(
    @NotNull LocalDate fecha,
    @NotNull LocalTime horaInicio,
    @NotNull LocalTime horaFin,
    @Size(max = 500) String descripcion,
    UUID asignacionId,
    UUID actividadId
) {}
