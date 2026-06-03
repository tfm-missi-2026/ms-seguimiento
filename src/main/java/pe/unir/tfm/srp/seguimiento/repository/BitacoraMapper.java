package pe.unir.tfm.srp.seguimiento.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import pe.unir.tfm.srp.seguimiento.model.Bitacora;

@Mapper
public interface BitacoraMapper {

    Bitacora buscarPorId(@Param("id") UUID id);

    List<Bitacora> listarPorUsuarioYFecha(@Param("usuarioId") UUID usuarioId,
                                          @Param("fecha") LocalDate fecha);

    List<Bitacora> listarPorUsuarioYRango(@Param("usuarioId") UUID usuarioId,
                                          @Param("desde") LocalDate desde,
                                          @Param("hasta") LocalDate hasta);

    void insertar(Bitacora bitacora);

    void actualizar(Bitacora bitacora);

    void eliminarLogico(@Param("id") UUID id,
                        @Param("usuarioEliminacion") UUID usuarioEliminacion,
                        @Param("motivoEliminacion") String motivoEliminacion);
}
