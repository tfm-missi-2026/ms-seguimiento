package pe.unir.tfm.srp.seguimiento.model;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvanceTareaAgregado {
    private UUID tareaId;
    private String nombre;
    private Short horasEstimadas;
    private BigDecimal horasRegistradas;
}
