package pe.unir.tfm.srp.seguimiento.client;

import java.time.LocalDate;

public record TareaFechasRequest(
    LocalDate fechaInicioPlanificada,
    LocalDate fechaFinPlanificada
) {}
