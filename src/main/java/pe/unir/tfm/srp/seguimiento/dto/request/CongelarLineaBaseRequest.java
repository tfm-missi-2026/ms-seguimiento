package pe.unir.tfm.srp.seguimiento.dto.request;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CongelarLineaBaseRequest(
    @NotNull UUID proyectoId,
    @Size(max = 500) String descripcion,
    @NotNull List<TareaSnapshot> tareas,
    @NotNull List<AsignacionSnapshot> asignaciones
) {
    public record TareaSnapshot(
        @NotNull UUID tareaId,
        @NotNull String nombre,
        String descripcion,
        @NotNull LocalDate fechaInicioPlanificada,
        @NotNull LocalDate fechaFinPlanificada,
        @NotNull Short horasEstimadas
    ) {}

    public record AsignacionSnapshot(
        @NotNull UUID asignacionId,
        @NotNull UUID tareaId,
        @NotNull UUID usuarioId
    ) {}
}
