package pe.unir.tfm.srp.seguimiento.dto.conversor;

import java.util.List;

import org.mapstruct.Mapper;

import pe.unir.tfm.srp.seguimiento.dto.response.VariacionResponse;
import pe.unir.tfm.srp.seguimiento.model.Variacion;

@Mapper
public interface VariacionConversor {
    VariacionResponse aVariacionResponse(Variacion variacion);
    List<VariacionResponse> aVariacionResponseList(List<Variacion> variaciones);
}
