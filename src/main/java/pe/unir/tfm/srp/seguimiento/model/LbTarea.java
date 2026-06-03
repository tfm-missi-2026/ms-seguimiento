package pe.unir.tfm.srp.seguimiento.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LbTarea {
    private UUID id;
    private UUID lineaBaseId;
    private UUID tareaId;
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicioPlanificada;
    private LocalDate fechaFinPlanificada;
    private Short horasEstimadas;
    private Short estado;
    private LocalDateTime fechaCreacion;
    private UUID usuarioCreacion;
    private LocalDateTime fechaModificacion;
    private UUID usuarioModificacion;
    private LocalDateTime fechaEliminacion;
    private UUID usuarioEliminacion;
    private String motivoEliminacion;
}
