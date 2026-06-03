package pe.unir.tfm.srp.seguimiento.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record VariacionResponse(
    UUID id,
    UUID tareaId,
    UUID tipoVariacionId,
    String descripcion,
    String justificacion,
    String valorAnterior,
    String valorNuevo,
    LocalDate fechaDeteccion,
    UUID reportadaPor,
    UUID situacionId,
    String observacionResolucion,
    LocalDateTime fechaResolucion,
    UUID resueltoPor,
    Short estado
) {}
