package pe.unir.tfm.srp.seguimiento.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import pe.unir.tfm.srp.seguimiento.model.CargaAgregado;

@Mapper
public interface CargaMapper {

    List<CargaAgregado> agregarCargaEquipo(@Param("desde") LocalDate desde,
                                           @Param("hasta") LocalDate hasta,
                                           @Param("tareaIds") List<UUID> tareaIds);

    CargaAgregado agregarCargaPorUsuario(@Param("usuarioId") UUID usuarioId,
                                         @Param("desde") LocalDate desde,
                                         @Param("hasta") LocalDate hasta,
                                         @Param("tareaIds") List<UUID> tareaIds);
}
