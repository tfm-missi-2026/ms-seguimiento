package pe.unir.tfm.srp.seguimiento.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record LineaBaseResponse(
    UUID id,
    UUID proyectoId,
    Short version,
    String descripcion,
    LocalDateTime fechaCongelacion,
    UUID congeladaPor,
    List<LbTareaResponse> tareas,
    List<LbAsignacionResponse> asignaciones
) {}
