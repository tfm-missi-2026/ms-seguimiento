package pe.unir.tfm.srp.seguimiento.dto.conversor;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import pe.unir.tfm.srp.seguimiento.dto.response.LbAsignacionResponse;
import pe.unir.tfm.srp.seguimiento.dto.response.LbTareaResponse;
import pe.unir.tfm.srp.seguimiento.dto.response.LineaBaseResponse;
import pe.unir.tfm.srp.seguimiento.model.LbAsignacion;
import pe.unir.tfm.srp.seguimiento.model.LbTarea;
import pe.unir.tfm.srp.seguimiento.model.LineaBase;

@Mapper
public interface LineaBaseConversor {

    LbTareaResponse aLbTareaResponse(LbTarea tarea);

    List<LbTareaResponse> aLbTareaResponseList(List<LbTarea> tareas);

    LbAsignacionResponse aLbAsignacionResponse(LbAsignacion asignacion);

    List<LbAsignacionResponse> aLbAsignacionResponseList(List<LbAsignacion> asignaciones);

    @Mapping(target = "tareas", source = "tareas")
    @Mapping(target = "asignaciones", source = "asignaciones")
    LineaBaseResponse aLineaBaseResponse(LineaBase lineaBase,
                                          List<LbTareaResponse> tareas,
                                          List<LbAsignacionResponse> asignaciones);
}
