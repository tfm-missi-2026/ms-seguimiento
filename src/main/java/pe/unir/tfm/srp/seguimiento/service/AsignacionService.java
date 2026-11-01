package pe.unir.tfm.srp.seguimiento.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.seguimiento.client.ProyectosClient;
import pe.unir.tfm.srp.seguimiento.client.TareaFechasRequest;
import pe.unir.tfm.srp.seguimiento.client.TareaProyectoResponse;
import pe.unir.tfm.srp.seguimiento.config.CurrentUserResolver;
import pe.unir.tfm.srp.seguimiento.dto.conversor.AsignacionConversor;
import pe.unir.tfm.srp.seguimiento.dto.request.AsignacionRequest;
import pe.unir.tfm.srp.seguimiento.dto.request.EliminacionRequest;
import pe.unir.tfm.srp.seguimiento.dto.response.AsignacionResponse;
import pe.unir.tfm.srp.seguimiento.exception.ConflictoNegocioException;
import pe.unir.tfm.srp.seguimiento.exception.RecursoNoEncontradoException;
import pe.unir.tfm.srp.seguimiento.model.Asignacion;
import pe.unir.tfm.srp.seguimiento.repository.AsignacionMapper;
import pe.unir.tfm.srp.seguimiento.repository.LineaBaseMapper;

@Service
@RequiredArgsConstructor
public class AsignacionService {

    // Jornada base de 8 horas por dia habil (L-V), segun el modelo de datos de la linea base
    private static final int JORNADA_BASE_HORAS = 8;

    private final AsignacionMapper asignacionMapper;
    private final LineaBaseMapper lineaBaseMapper;
    private final ProyectosClient proyectosClient;
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
        if (asignacionMapper.contarPorTarea(request.tareaId()) > 0) {
            throw new ConflictoNegocioException("La tarea ya esta asignada a un recurso");
        }
        LocalDate inicio = request.fechaInicioPlanificada();
        validarInicioHabil(inicio);
        TareaProyectoResponse tarea = obtenerTarea(request.tareaId());
        LocalDate fin = calcularFechaFin(inicio, tarea.diasEstimados());
        BigDecimal horas = BigDecimal.valueOf(tarea.horasEstimadas());
        validarCapacidad(request, horas, fin);
        Asignacion nueva = Asignacion.builder()
                .id(UUID.randomUUID())
                .tareaId(request.tareaId())
                .usuarioId(request.usuarioId())
                .horasPlanificadas(horas)
                .fechaInicioPlanificada(inicio)
                .fechaFinPlanificada(fin)
                .build();
        asignacionMapper.insertar(nueva);
        lineaBaseMapper.completarFechasTarea(request.tareaId(), inicio, fin,
                currentUserResolver.obtenerUsuarioActualId());
        proyectosClient.actualizarFechas(request.tareaId(), new TareaFechasRequest(inicio, fin));
        return asignacionConversor.aAsignacionResponse(nueva);
    }

    private void validarInicioHabil(LocalDate inicio) {
        if (!esDiaHabil(inicio)) {
            throw new ConflictoNegocioException("La fecha de inicio debe ser un dia habil");
        }
    }

    private TareaProyectoResponse obtenerTarea(UUID tareaId) {
        try {
            return proyectosClient.obtenerTarea(tareaId);
        } catch (FeignException.NotFound ex) {
            throw new RecursoNoEncontradoException("Tarea " + tareaId + " no encontrada");
        }
    }

    private LocalDate calcularFechaFin(LocalDate inicio, int dias) {
        LocalDate fin = inicio;
        int restantes = dias - 1;
        while (restantes > 0) {
            fin = fin.plusDays(1);
            if (esDiaHabil(fin)) {
                restantes--;
            }
        }
        return fin;
    }

    private boolean esDiaHabil(LocalDate dia) {
        DayOfWeek dow = dia.getDayOfWeek();
        return dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY;
    }

    // RF-06: advierte si la carga del recurso en el periodo supera su capacidad (jornada base
    // x dias habiles). El alta solo procede si el solicitante confirma la sobrecarga.
    private void validarCapacidad(AsignacionRequest request, BigDecimal horas, LocalDate fin) {
        if (request.confirmarSobrecarga()) {
            return;
        }
        BigDecimal capacidad = capacidadDelPeriodo(request.fechaInicioPlanificada(), fin);
        BigDecimal cargaExistente = asignacionMapper.sumarHorasPlanificadasEnPeriodo(
                request.usuarioId(), request.fechaInicioPlanificada(), fin);
        BigDecimal cargaTotal = cargaExistente.add(horas);
        if (cargaTotal.compareTo(capacidad) > 0) {
            throw new ConflictoNegocioException(
                    "La asignacion deja al recurso en sobrecarga: " + cargaTotal + " h planificadas frente a "
                            + capacidad + " h de capacidad en el periodo. Reenvie con confirmarSobrecarga=true para continuar");
        }
    }

    private BigDecimal capacidadDelPeriodo(LocalDate desde, LocalDate hasta) {
        long diasHabiles = 0;
        for (LocalDate dia = desde; !dia.isAfter(hasta); dia = dia.plusDays(1)) {
            if (esDiaHabil(dia)) {
                diasHabiles++;
            }
        }
        return BigDecimal.valueOf(diasHabiles * JORNADA_BASE_HORAS);
    }

    @Transactional
    public void eliminar(UUID id, EliminacionRequest request) {
        if (asignacionMapper.buscarPorId(id) == null) {
            throw new RecursoNoEncontradoException("Asignacion " + id + " no encontrada");
        }
        asignacionMapper.eliminarLogico(id, currentUserResolver.obtenerUsuarioActualId(), request.motivoEliminacion());
    }
}
