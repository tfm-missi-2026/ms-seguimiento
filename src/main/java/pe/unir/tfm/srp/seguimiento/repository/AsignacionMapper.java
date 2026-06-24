package pe.unir.tfm.srp.seguimiento.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import pe.unir.tfm.srp.seguimiento.model.Asignacion;

@Mapper
public interface AsignacionMapper {

    Asignacion buscarPorId(@Param("id") UUID id);

    List<Asignacion> listarActivas();

    List<Asignacion> listarPorUsuario(@Param("usuarioId") UUID usuarioId);

    List<Asignacion> listarPorTarea(@Param("tareaId") UUID tareaId);

    int contar(@Param("tareaId") UUID tareaId, @Param("usuarioId") UUID usuarioId);

    BigDecimal sumarHorasPlanificadasEnPeriodo(@Param("usuarioId") UUID usuarioId,
                                               @Param("desde") LocalDate desde,
                                               @Param("hasta") LocalDate hasta);

    void insertar(Asignacion asignacion);

    void actualizar(Asignacion asignacion);

    void eliminarLogico(@Param("id") UUID id,
                        @Param("usuarioEliminacion") UUID usuarioEliminacion,
                        @Param("motivoEliminacion") String motivoEliminacion);
}
