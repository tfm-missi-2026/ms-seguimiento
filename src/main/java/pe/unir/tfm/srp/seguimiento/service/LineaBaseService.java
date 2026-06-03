package pe.unir.tfm.srp.seguimiento.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.seguimiento.config.CurrentUserResolver;
import pe.unir.tfm.srp.seguimiento.dto.conversor.LineaBaseConversor;
import pe.unir.tfm.srp.seguimiento.dto.request.CongelarLineaBaseRequest;
import pe.unir.tfm.srp.seguimiento.dto.response.LbAsignacionResponse;
import pe.unir.tfm.srp.seguimiento.dto.response.LbTareaResponse;
import pe.unir.tfm.srp.seguimiento.dto.response.LineaBaseResponse;
import pe.unir.tfm.srp.seguimiento.exception.RecursoNoEncontradoException;
import pe.unir.tfm.srp.seguimiento.model.LbAsignacion;
import pe.unir.tfm.srp.seguimiento.model.LbTarea;
import pe.unir.tfm.srp.seguimiento.model.LineaBase;
import pe.unir.tfm.srp.seguimiento.repository.LineaBaseMapper;

@Service
@RequiredArgsConstructor
public class LineaBaseService {

    private final LineaBaseMapper lineaBaseMapper;
    private final LineaBaseConversor lineaBaseConversor;
    private final CurrentUserResolver currentUserResolver;

    public List<LineaBase> listarPorProyecto(UUID proyectoId) {
        return lineaBaseMapper.listarPorProyecto(proyectoId);
    }

    public LineaBaseResponse buscarPorId(UUID id) {
        LineaBase cabecera = lineaBaseMapper.buscarPorId(id);
        if (cabecera == null) {
            throw new RecursoNoEncontradoException("Linea base " + id + " no encontrada");
        }
        List<LbTareaResponse> tareas = lineaBaseConversor.aLbTareaResponseList(
                lineaBaseMapper.listarTareasPorLineaBase(id));
        List<LbAsignacionResponse> asignaciones = lineaBaseConversor.aLbAsignacionResponseList(
                lineaBaseMapper.listarAsignacionesPorLineaBase(id));
        return lineaBaseConversor.aLineaBaseResponse(cabecera, tareas, asignaciones);
    }

    @Transactional
    public LineaBaseResponse congelar(CongelarLineaBaseRequest request) {
        UUID actor = currentUserResolver.obtenerUsuarioActualId();
        Short version = lineaBaseMapper.obtenerSiguienteVersion(request.proyectoId());

        LineaBase cabecera = LineaBase.builder()
                .id(UUID.randomUUID())
                .proyectoId(request.proyectoId())
                .version(version)
                .descripcion(request.descripcion())
                .fechaCongelacion(LocalDateTime.now())
                .congeladaPor(actor)
                .build();
        lineaBaseMapper.insertarCabecera(cabecera);

        request.tareas().forEach(t -> {
            LbTarea lb = LbTarea.builder()
                    .id(UUID.randomUUID())
                    .lineaBaseId(cabecera.getId())
                    .tareaId(t.tareaId())
                    .nombre(t.nombre())
                    .descripcion(t.descripcion())
                    .fechaInicioPlanificada(t.fechaInicioPlanificada())
                    .fechaFinPlanificada(t.fechaFinPlanificada())
                    .horasEstimadas(t.horasEstimadas())
                    .build();
            lineaBaseMapper.insertarTarea(lb);
        });

        request.asignaciones().forEach(a -> {
            LbAsignacion lb = LbAsignacion.builder()
                    .id(UUID.randomUUID())
                    .lineaBaseId(cabecera.getId())
                    .asignacionId(a.asignacionId())
                    .tareaId(a.tareaId())
                    .usuarioId(a.usuarioId())
                    .build();
            lineaBaseMapper.insertarAsignacion(lb);
        });

        return buscarPorId(cabecera.getId());
    }
}
