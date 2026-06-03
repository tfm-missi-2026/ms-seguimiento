package pe.unir.tfm.srp.seguimiento.dto.conversor;

import java.util.List;

import org.mapstruct.Mapper;

import pe.unir.tfm.srp.seguimiento.dto.response.AsignacionResponse;
import pe.unir.tfm.srp.seguimiento.model.Asignacion;

@Mapper
public interface AsignacionConversor {
    AsignacionResponse aAsignacionResponse(Asignacion asignacion);
    List<AsignacionResponse> aAsignacionResponseList(List<Asignacion> asignaciones);
}
