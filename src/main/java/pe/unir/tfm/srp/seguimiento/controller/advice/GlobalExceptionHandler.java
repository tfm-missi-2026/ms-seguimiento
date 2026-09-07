package pe.unir.tfm.srp.seguimiento.controller.advice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import pe.unir.tfm.srp.seguimiento.exception.ConflictoNegocioException;
import pe.unir.tfm.srp.seguimiento.exception.RecursoNoEncontradoException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail manejarValidacion(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<Map<String, String>> errores = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> {
                    Map<String, String> m = new HashMap<>();
                    m.put("campo", fe.getField());
                    m.put("mensaje", fe.getDefaultMessage());
                    return m;
                })
                .toList();

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Datos invalidos");
        problem.setTitle("VALIDACION_FALLIDA");
        problem.setInstance(URI.create(req.getRequestURI()));
        problem.setProperty("timestamp", Instant.now().toString());
        problem.setProperty("errores", errores);
        return problem;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail manejarCuerpoIlegible(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return crearProblema(HttpStatus.BAD_REQUEST, "VALIDACION_FALLIDA",
                "El cuerpo de la peticion no tiene un formato valido", req);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail manejarTipoInvalido(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        Map<String, String> error = new HashMap<>();
        error.put("campo", ex.getName());
        error.put("mensaje", "El valor recibido no tiene el formato esperado");

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Datos invalidos");
        problem.setTitle("VALIDACION_FALLIDA");
        problem.setInstance(URI.create(req.getRequestURI()));
        problem.setProperty("timestamp", Instant.now().toString());
        problem.setProperty("errores", List.of(error));
        return problem;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail manejarAccesoDenegado(AccessDeniedException ex, HttpServletRequest req) {
        return crearProblema(HttpStatus.FORBIDDEN, "ACCESO_DENEGADO", "Acceso denegado", req);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ProblemDetail manejarNoEncontrado(RecursoNoEncontradoException ex, HttpServletRequest req) {
        return crearProblema(HttpStatus.NOT_FOUND, "RECURSO_NO_ENCONTRADO", ex.getMessage(), req);
    }

    @ExceptionHandler(ConflictoNegocioException.class)
    public ProblemDetail manejarConflicto(ConflictoNegocioException ex, HttpServletRequest req) {
        return crearProblema(HttpStatus.CONFLICT, "CONFLICTO_NEGOCIO", ex.getMessage(), req);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail manejarIntegridad(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("Violacion de integridad en {}: {}", req.getRequestURI(), ex.getMostSpecificCause().getMessage());
        return crearProblema(HttpStatus.CONFLICT, "CONFLICTO_INTEGRIDAD",
                "Ya existe un registro con esos datos.", req);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail manejarErrorGenerico(Exception ex, HttpServletRequest req) {
        log.error("Error no controlado en {}", req.getRequestURI(), ex);
        return crearProblema(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_INTERNO", "Error interno del servidor", req);
    }

    private ProblemDetail crearProblema(HttpStatus status, String codigo, String detalle, HttpServletRequest req) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detalle);
        problem.setTitle(codigo);
        problem.setInstance(URI.create(req.getRequestURI()));
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }
}
