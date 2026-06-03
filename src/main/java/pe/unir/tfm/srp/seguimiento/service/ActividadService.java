package pe.unir.tfm.srp.seguimiento.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.seguimiento.config.CurrentUserResolver;
import pe.unir.tfm.srp.seguimiento.dto.conversor.ActividadConversor;
import pe.unir.tfm.srp.seguimiento.dto.request.ActividadRequest;
import pe.unir.tfm.srp.seguimiento.dto.request.EliminacionRequest;
import pe.unir.tfm.srp.seguimiento.dto.response.ActividadResponse;
import pe.unir.tfm.srp.seguimiento.exception.RecursoNoEncontradoException;
import pe.unir.tfm.srp.seguimiento.model.Actividad;
import pe.unir.tfm.srp.seguimiento.repository.ActividadMapper;

@Service
@RequiredArgsConstructor
public class ActividadService {

    private final ActividadMapper actividadMapper;
    private final ActividadConversor actividadConversor;
    private final CurrentUserResolver currentUserResolver;

    public List<ActividadResponse> listar() {
        return actividadConversor.aActividadResponseList(actividadMapper.listarActivas());
    }

    public List<ActividadResponse> listarPorFecha(LocalDate fecha) {
        return actividadConversor.aActividadResponseList(actividadMapper.listarPorFecha(fecha));
    }

    public ActividadResponse buscarPorId(UUID id) {
        Actividad actividad = actividadMapper.buscarPorId(id);
        if (actividad == null) {
            throw new RecursoNoEncontradoException("Actividad " + id + " no encontrada");
        }
        return actividadConversor.aActividadResponse(actividad);
    }

    @Transactional
    public ActividadResponse crear(ActividadRequest request) {
        Actividad nueva = Actividad.builder()
                .id(UUID.randomUUID())
                .tipoActividadId(request.tipoActividadId())
                .modalidadId(request.modalidadId())
                .titulo(request.titulo())
                .descripcion(request.descripcion())
                .fecha(request.fecha())
                .horaInicio(request.horaInicio())
                .horaFin(request.horaFin())
                .organizadorId(request.organizadorId())
                .build();
        actividadMapper.insertar(nueva);
        return actividadConversor.aActividadResponse(nueva);
    }

    @Transactional
    public ActividadResponse actualizar(UUID id, ActividadRequest request) {
        Actividad existente = actividadMapper.buscarPorId(id);
        if (existente == null) {
            throw new RecursoNoEncontradoException("Actividad " + id + " no encontrada");
        }
        existente.setTipoActividadId(request.tipoActividadId());
        existente.setModalidadId(request.modalidadId());
        existente.setTitulo(request.titulo());
        existente.setDescripcion(request.descripcion());
        existente.setFecha(request.fecha());
        existente.setHoraInicio(request.horaInicio());
        existente.setHoraFin(request.horaFin());
        existente.setOrganizadorId(request.organizadorId());
        actividadMapper.actualizar(existente);
        return actividadConversor.aActividadResponse(existente);
    }

    @Transactional
    public void eliminar(UUID id, EliminacionRequest request) {
        if (actividadMapper.buscarPorId(id) == null) {
            throw new RecursoNoEncontradoException("Actividad " + id + " no encontrada");
        }
        actividadMapper.eliminarLogico(id, currentUserResolver.obtenerUsuarioActualId(), request.motivoEliminacion());
    }
}
