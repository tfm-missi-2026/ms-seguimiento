package pe.unir.tfm.srp.seguimiento.dto.conversor;

import java.util.List;

import org.mapstruct.Mapper;

import pe.unir.tfm.srp.seguimiento.dto.response.BitacoraResponse;
import pe.unir.tfm.srp.seguimiento.model.Bitacora;

@Mapper
public interface BitacoraConversor {
    BitacoraResponse aBitacoraResponse(Bitacora bitacora);
    List<BitacoraResponse> aBitacoraResponseList(List<Bitacora> bitacoras);
}
