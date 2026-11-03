package pe.unir.tfm.srp.seguimiento.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.seguimiento.config.CurrentUserResolver;
import pe.unir.tfm.srp.seguimiento.dto.conversor.VariacionConversor;
import pe.unir.tfm.srp.seguimiento.dto.request.EliminacionRequest;
import pe.unir.tfm.srp.seguimiento.dto.request.VariacionCrearRequest;
import pe.unir.tfm.srp.seguimiento.dto.request.VariacionResolverRequest;
import pe.unir.tfm.srp.seguimiento.dto.response.VariacionResponse;
import pe.unir.tfm.srp.seguimiento.exception.ConflictoNegocioException;
import pe.unir.tfm.srp.seguimiento.exception.RecursoNoEncontradoException;
import pe.unir.tfm.srp.seguimiento.model.Variacion;
import pe.unir.tfm.srp.seguimiento.repository.LineaBaseMapper;
import pe.unir.tfm.srp.seguimiento.repository.VariacionMapper;

@Service
@RequiredArgsConstructor
public class VariacionService {

    private final VariacionMapper variacionMapper;
    private final LineaBaseMapper lineaBaseMapper;
    private final VariacionConversor variacionConversor;
    private final CurrentUserResolver currentUserResolver;

    public List<VariacionResponse> listar() {
        return variacionConversor.aVariacionResponseList(variacionMapper.listarActivas());
    }

    public List<VariacionResponse> listarPorTarea(UUID tareaId) {
        return variacionConversor.aVariacionResponseList(variacionMapper.listarPorTarea(tareaId));
    }

    public VariacionResponse buscarPorId(UUID id) {
        Variacion v = variacionMapper.buscarPorId(id);
        if (v == null) {
            throw new RecursoNoEncontradoException("Variacion " + id + " no encontrada");
        }
        return variacionConversor.aVariacionResponse(v);
    }

    @Transactional
    public VariacionResponse crear(VariacionCrearRequest request) {
        if (lineaBaseMapper.contarTareaCongelada(request.tareaId()) == 0) {
            throw new ConflictoNegocioException("La tarea no pertenece a una linea base congelada");
        }
        Variacion nueva = Variacion.builder()
                .id(UUID.randomUUID())
                .tareaId(request.tareaId())
                .tipoVariacionId(request.tipoVariacionId())
                .descripcion(request.descripcion())
                .justificacion(request.justificacion())
                .valorAnterior(request.valorAnterior())
                .valorNuevo(request.valorNuevo())
                .fechaDeteccion(request.fechaDeteccion())
                .reportadaPor(request.reportadaPor())
                .build();
        variacionMapper.insertar(nueva);
        return variacionConversor.aVariacionResponse(nueva);
    }

    @Transactional
    public VariacionResponse resolver(UUID id, VariacionResolverRequest request) {
        Variacion existente = variacionMapper.buscarPorId(id);
        if (existente == null) {
            throw new RecursoNoEncontradoException("Variacion " + id + " no encontrada");
        }
        existente.setSituacionId(request.situacionId());
        existente.setObservacionResolucion(request.observacionResolucion());
        existente.setFechaResolucion(LocalDateTime.now());
        existente.setResueltoPor(currentUserResolver.obtenerUsuarioActualId());
        variacionMapper.actualizar(existente);
        return variacionConversor.aVariacionResponse(existente);
    }

    @Transactional
    public void eliminar(UUID id, EliminacionRequest request) {
        Variacion existente = variacionMapper.buscarPorId(id);
        if (existente == null) {
            throw new RecursoNoEncontradoException("Variacion " + id + " no encontrada");
        }
        if (existente.getResueltoPor() != null) {
            throw new ConflictoNegocioException("Solo se pueden eliminar variaciones pendientes");
        }
        variacionMapper.eliminarLogico(id, currentUserResolver.obtenerUsuarioActualId(), request.motivoEliminacion());
    }
}
