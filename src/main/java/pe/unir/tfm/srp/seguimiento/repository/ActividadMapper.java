package pe.unir.tfm.srp.seguimiento.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import pe.unir.tfm.srp.seguimiento.model.Actividad;

@Mapper
public interface ActividadMapper {

    Actividad buscarPorId(@Param("id") UUID id);

    List<Actividad> listarActivas();

    List<Actividad> listarPorFecha(@Param("fecha") LocalDate fecha);

    void insertar(Actividad actividad);

    void actualizar(Actividad actividad);

    void eliminarLogico(@Param("id") UUID id,
                        @Param("usuarioEliminacion") UUID usuarioEliminacion,
                        @Param("motivoEliminacion") String motivoEliminacion);
}
