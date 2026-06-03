package pe.unir.tfm.srp.seguimiento.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ActividadResponse(
    UUID id,
    UUID tipoActividadId,
    UUID modalidadId,
    String titulo,
    String descripcion,
    LocalDate fecha,
    LocalTime horaInicio,
    LocalTime horaFin,
    UUID organizadorId,
    Short estado
) {}
