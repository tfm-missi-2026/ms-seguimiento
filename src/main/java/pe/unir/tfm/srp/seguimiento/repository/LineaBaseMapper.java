package pe.unir.tfm.srp.seguimiento.repository;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import pe.unir.tfm.srp.seguimiento.model.LbAsignacion;
import pe.unir.tfm.srp.seguimiento.model.LbTarea;
import pe.unir.tfm.srp.seguimiento.model.LineaBase;

@Mapper
public interface LineaBaseMapper {

    LineaBase buscarPorId(@Param("id") UUID id);

    List<LineaBase> listarPorProyecto(@Param("proyectoId") UUID proyectoId);

    Short obtenerSiguienteVersion(@Param("proyectoId") UUID proyectoId);

    void insertarCabecera(LineaBase lineaBase);

    void insertarTarea(LbTarea lbTarea);

    void insertarAsignacion(LbAsignacion lbAsignacion);

    List<LbTarea> listarTareasPorLineaBase(@Param("lineaBaseId") UUID lineaBaseId);

    List<LbAsignacion> listarAsignacionesPorLineaBase(@Param("lineaBaseId") UUID lineaBaseId);
}
