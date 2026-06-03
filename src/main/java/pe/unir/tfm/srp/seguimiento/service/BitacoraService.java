package pe.unir.tfm.srp.seguimiento.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.seguimiento.config.CurrentUserResolver;
import pe.unir.tfm.srp.seguimiento.dto.conversor.BitacoraConversor;
import pe.unir.tfm.srp.seguimiento.dto.request.BitacoraRequest;
import pe.unir.tfm.srp.seguimiento.dto.request.EliminacionRequest;
import pe.unir.tfm.srp.seguimiento.dto.response.BitacoraResponse;
import pe.unir.tfm.srp.seguimiento.exception.ConflictoNegocioException;
import pe.unir.tfm.srp.seguimiento.exception.RecursoNoEncontradoException;
import pe.unir.tfm.srp.seguimiento.model.Bitacora;
import pe.unir.tfm.srp.seguimiento.repository.BitacoraMapper;

@Service
@RequiredArgsConstructor
public class BitacoraService {

    private final BitacoraMapper bitacoraMapper;
    private final BitacoraConversor bitacoraConversor;
    private final CurrentUserResolver currentUserResolver;

    public List<BitacoraResponse> listarPorUsuarioYFecha(UUID usuarioId, LocalDate fecha) {
        return bitacoraConversor.aBitacoraResponseList(bitacoraMapper.listarPorUsuarioYFecha(usuarioId, fecha));
    }

    public List<BitacoraResponse> listarPorUsuarioYRango(UUID usuarioId, LocalDate desde, LocalDate hasta) {
        return bitacoraConversor.aBitacoraResponseList(bitacoraMapper.listarPorUsuarioYRango(usuarioId, desde, hasta));
    }

    public BitacoraResponse buscarPorId(UUID id) {
        Bitacora entrada = bitacoraMapper.buscarPorId(id);
        if (entrada == null) {
            throw new RecursoNoEncontradoException("Entrada de bitacora " + id + " no encontrada");
        }
        return bitacoraConversor.aBitacoraResponse(entrada);
    }

    @Transactional
    public BitacoraResponse crear(BitacoraRequest request) {
        validarXor(request.asignacionId(), request.actividadId());
        validarHoras(request);
        Bitacora nueva = Bitacora.builder()
                .id(UUID.randomUUID())
                .usuarioId(currentUserResolver.obtenerUsuarioActualId())
                .fecha(request.fecha())
                .horaInicio(request.horaInicio())
                .horaFin(request.horaFin())
                .descripcion(request.descripcion())
                .asignacionId(request.asignacionId())
                .actividadId(request.actividadId())
                .build();
        bitacoraMapper.insertar(nueva);
        return bitacoraConversor.aBitacoraResponse(nueva);
    }

    @Transactional
    public BitacoraResponse actualizar(UUID id, BitacoraRequest request) {
        Bitacora existente = bitacoraMapper.buscarPorId(id);
        if (existente == null) {
            throw new RecursoNoEncontradoException("Entrada de bitacora " + id + " no encontrada");
        }
        validarXor(request.asignacionId(), request.actividadId());
        validarHoras(request);
        existente.setFecha(request.fecha());
        existente.setHoraInicio(request.horaInicio());
        existente.setHoraFin(request.horaFin());
        existente.setDescripcion(request.descripcion());
        existente.setAsignacionId(request.asignacionId());
        existente.setActividadId(request.actividadId());
        bitacoraMapper.actualizar(existente);
        return bitacoraConversor.aBitacoraResponse(existente);
    }

    @Transactional
    public void eliminar(UUID id, EliminacionRequest request) {
        if (bitacoraMapper.buscarPorId(id) == null) {
            throw new RecursoNoEncontradoException("Entrada de bitacora " + id + " no encontrada");
        }
        bitacoraMapper.eliminarLogico(id, currentUserResolver.obtenerUsuarioActualId(), request.motivoEliminacion());
    }

    private void validarXor(UUID asignacionId, UUID actividadId) {
        boolean aA = asignacionId != null;
        boolean aB = actividadId != null;
        if (aA == aB) {
            throw new ConflictoNegocioException(
                    "Exactamente uno entre asignacion_id y actividad_id debe estar presente");
        }
    }

    private void validarHoras(BitacoraRequest request) {
        if (!request.horaFin().isAfter(request.horaInicio())) {
            throw new ConflictoNegocioException("hora_fin debe ser posterior a hora_inicio");
        }
    }
}
