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
public class CargaAgregado {
    private UUID usuarioId;
    private BigDecimal horasPlanificadas;
    private BigDecimal horasRegistradas;
    private long numeroTareasActivas;
}
