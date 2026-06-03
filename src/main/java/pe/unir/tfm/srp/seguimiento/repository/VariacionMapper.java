package pe.unir.tfm.srp.seguimiento.repository;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import pe.unir.tfm.srp.seguimiento.model.Variacion;

@Mapper
public interface VariacionMapper {

    Variacion buscarPorId(@Param("id") UUID id);

    List<Variacion> listarActivas();

    List<Variacion> listarPorTarea(@Param("tareaId") UUID tareaId);

    void insertar(Variacion variacion);

    void actualizar(Variacion variacion);

    void eliminarLogico(@Param("id") UUID id,
                        @Param("usuarioEliminacion") UUID usuarioEliminacion,
                        @Param("motivoEliminacion") String motivoEliminacion);
}
