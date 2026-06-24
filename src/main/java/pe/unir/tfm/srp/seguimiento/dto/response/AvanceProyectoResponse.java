package pe.unir.tfm.srp.seguimiento.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record AvanceProyectoResponse(
    UUID proyectoId,
    UUID lineaBaseId,
    Short version,
    BigDecimal horasEstimadasTotal,
    BigDecimal horasRegistradasTotal,
    BigDecimal porcentajeAvance,
    List<AvanceTareaResponse> tareas
) {}
