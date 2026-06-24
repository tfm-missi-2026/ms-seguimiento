package pe.unir.tfm.srp.seguimiento.repository;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import pe.unir.tfm.srp.seguimiento.model.AvanceTareaAgregado;

@Mapper
public interface AvanceMapper {

    List<AvanceTareaAgregado> avancePorLineaBase(@Param("lineaBaseId") UUID lineaBaseId);
}
