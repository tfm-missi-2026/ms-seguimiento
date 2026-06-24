package pe.unir.tfm.srp.seguimiento.model;

import java.math.BigDecimal;
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
public class Asignacion {
    private UUID id;
    private UUID tareaId;
    private UUID usuarioId;
    private BigDecimal horasPlanificadas;
    private LocalDate fechaInicioPlanificada;
    private LocalDate fechaFinPlanificada;
    private Short estado;
    private LocalDateTime fechaCreacion;
    private UUID usuarioCreacion;
    private LocalDateTime fechaModificacion;
    private UUID usuarioModificacion;
    private LocalDateTime fechaEliminacion;
    private UUID usuarioEliminacion;
    private String motivoEliminacion;
}
