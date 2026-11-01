package pe.unir.tfm.srp.seguimiento.client;

import java.util.UUID;

public record TareaProyectoResponse(
    UUID id,
    Short diasEstimados,
    Short horasEstimadas
) {}
