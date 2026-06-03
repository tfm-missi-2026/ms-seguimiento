package pe.unir.tfm.srp.seguimiento.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.seguimiento.config.CurrentUserResolver;
import pe.unir.tfm.srp.seguimiento.dto.conversor.AsignacionConversor;
import pe.unir.tfm.srp.seguimiento.dto.request.AsignacionRequest;
import pe.unir.tfm.srp.seguimiento.dto.request.EliminacionRequest;
import pe.unir.tfm.srp.seguimiento.dto.response.AsignacionResponse;
import pe.unir.tfm.srp.seguimiento.exception.ConflictoNegocioException;
import pe.unir.tfm.srp.seguimiento.exception.RecursoNoEncontradoException;
import pe.unir.tfm.srp.seguimiento.model.Asignacion;
import pe.unir.tfm.srp.seguimiento.repository.AsignacionMapper;

@Service
@RequiredArgsConstructor
public class AsignacionService {

    private final AsignacionMapper asignacionMapper;
    private final AsignacionConversor asignacionConversor;
    private final CurrentUserResolver currentUserResolver;

    public List<AsignacionResponse> listar() {
        return asignacionConversor.aAsignacionResponseList(asignacionMapper.listarActivas());
    }

    public List<AsignacionResponse> listarPorUsuario(UUID usuarioId) {
        return asignacionConversor.aAsignacionResponseList(asignacionMapper.listarPorUsuario(usuarioId));
    }

    public List<AsignacionResponse> listarPorTarea(UUID tareaId) {
        return asignacionConversor.aAsignacionResponseList(asignacionMapper.listarPorTarea(tareaId));
    }

    public AsignacionResponse buscarPorId(UUID id) {
        Asignacion asig = asignacionMapper.buscarPorId(id);
        if (asig == null) {
            throw new RecursoNoEncontradoException("Asignacion " + id + " no encontrada");
        }
        return asignacionConversor.aAsignacionResponse(asig);
    }

    @Transactional
    public AsignacionResponse crear(AsignacionRequest request) {
        if (asignacionMapper.contar(request.tareaId(), request.usuarioId()) > 0) {
            throw new ConflictoNegocioException(
                    "Ya existe una asignacion para tarea=" + request.tareaId() + " usuario=" + request.usuarioId());
        }
        Asignacion nueva = Asignacion.builder()
                .id(UUID.randomUUID())
                .tareaId(request.tareaId())
                .usuarioId(request.usuarioId())
                .build();
        asignacionMapper.insertar(nueva);
        return asignacionConversor.aAsignacionResponse(nueva);
    }

    @Transactional
    public void eliminar(UUID id, EliminacionRequest request) {
        if (asignacionMapper.buscarPorId(id) == null) {
            throw new RecursoNoEncontradoException("Asignacion " + id + " no encontrada");
        }
        asignacionMapper.eliminarLogico(id, currentUserResolver.obtenerUsuarioActualId(), request.motivoEliminacion());
    }
}
