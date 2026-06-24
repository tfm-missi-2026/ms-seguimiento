package pe.unir.tfm.srp.seguimiento.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
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

    // Jornada base de 8 horas por dia habil (L-V), segun el modelo de datos de la linea base
    private static final int JORNADA_BASE_HORAS = 8;

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
        validarPeriodo(request);
        validarCapacidad(request);
        Asignacion nueva = Asignacion.builder()
                .id(UUID.randomUUID())
                .tareaId(request.tareaId())
                .usuarioId(request.usuarioId())
                .horasPlanificadas(request.horasPlanificadas())
                .fechaInicioPlanificada(request.fechaInicioPlanificada())
                .fechaFinPlanificada(request.fechaFinPlanificada())
                .build();
        asignacionMapper.insertar(nueva);
        return asignacionConversor.aAsignacionResponse(nueva);
    }

    private void validarPeriodo(AsignacionRequest request) {
        if (request.fechaInicioPlanificada() != null && request.fechaFinPlanificada() != null
                && request.fechaFinPlanificada().isBefore(request.fechaInicioPlanificada())) {
            throw new ConflictoNegocioException(
                    "fecha_fin_planificada debe ser posterior o igual a fecha_inicio_planificada");
        }
    }

    // RF-06: advierte si la carga del recurso en el periodo supera su capacidad (jornada base
    // x dias habiles). El alta solo procede si el solicitante confirma la sobrecarga.
    private void validarCapacidad(AsignacionRequest request) {
        if (request.confirmarSobrecarga()
                || request.horasPlanificadas() == null
                || request.fechaInicioPlanificada() == null
                || request.fechaFinPlanificada() == null) {
            return;
        }
        BigDecimal capacidad = capacidadDelPeriodo(
                request.fechaInicioPlanificada(), request.fechaFinPlanificada());
        BigDecimal cargaExistente = asignacionMapper.sumarHorasPlanificadasEnPeriodo(
                request.usuarioId(), request.fechaInicioPlanificada(), request.fechaFinPlanificada());
        BigDecimal cargaTotal = cargaExistente.add(request.horasPlanificadas());
        if (cargaTotal.compareTo(capacidad) > 0) {
            throw new ConflictoNegocioException(
                    "La asignacion deja al recurso en sobrecarga: " + cargaTotal + " h planificadas frente a "
                            + capacidad + " h de capacidad en el periodo. Reenvie con confirmarSobrecarga=true para continuar");
        }
    }

    private BigDecimal capacidadDelPeriodo(LocalDate desde, LocalDate hasta) {
        long diasHabiles = 0;
        for (LocalDate dia = desde; !dia.isAfter(hasta); dia = dia.plusDays(1)) {
            DayOfWeek dow = dia.getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
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
