package pe.unir.tfm.srp.seguimiento.model;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private LocalDate fechaInicioPlanificada;
    private LocalDate fechaFinPlanificada;
    private LocalDate primeraFechaRegistrada;
    private LocalDate ultimaFechaRegistrada;
    private Short horasEstimadas;
    private BigDecimal horasRegistradas;
}
