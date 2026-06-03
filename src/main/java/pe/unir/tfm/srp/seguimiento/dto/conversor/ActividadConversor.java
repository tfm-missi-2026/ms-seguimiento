package pe.unir.tfm.srp.seguimiento.dto.conversor;

import java.util.List;

import org.mapstruct.Mapper;

import pe.unir.tfm.srp.seguimiento.dto.response.ActividadResponse;
import pe.unir.tfm.srp.seguimiento.model.Actividad;

@Mapper
public interface ActividadConversor {
    ActividadResponse aActividadResponse(Actividad actividad);
    List<ActividadResponse> aActividadResponseList(List<Actividad> actividades);
}
