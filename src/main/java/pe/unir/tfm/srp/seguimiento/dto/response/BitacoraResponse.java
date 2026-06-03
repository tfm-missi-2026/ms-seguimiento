package pe.unir.tfm.srp.seguimiento.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record BitacoraResponse(
    UUID id,
    UUID usuarioId,
    LocalDate fecha,
    LocalTime horaInicio,
    LocalTime horaFin,
    String descripcion,
    UUID asignacionId,
    UUID actividadId,
    Short estado
) {}
