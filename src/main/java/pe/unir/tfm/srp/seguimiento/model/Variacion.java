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
public class Variacion {
    private UUID id;
    private UUID tareaId;
    private UUID tipoVariacionId;
    private String descripcion;
    private String justificacion;
    private String valorAnterior;
    private String valorNuevo;
    private LocalDate fechaDeteccion;
    private UUID reportadaPor;
    private UUID situacionId;
    private String observacionResolucion;
    private LocalDateTime fechaResolucion;
    private UUID resueltoPor;
    private Short estado;
    private LocalDateTime fechaCreacion;
    private UUID usuarioCreacion;
    private LocalDateTime fechaModificacion;
    private UUID usuarioModificacion;
    private LocalDateTime fechaEliminacion;
    private UUID usuarioEliminacion;
    private String motivoEliminacion;
}
