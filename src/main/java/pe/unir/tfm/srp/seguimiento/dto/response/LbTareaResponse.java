package pe.unir.tfm.srp.seguimiento.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record LbTareaResponse(
    UUID id,
    UUID lineaBaseId,
    UUID tareaId,
    String nombre,
    String descripcion,
    LocalDate fechaInicioPlanificada,
    LocalDate fechaFinPlanificada,
    Short horasEstimadas
) {}
