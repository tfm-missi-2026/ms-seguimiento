package pe.unir.tfm.srp.seguimiento.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.seguimiento.dto.request.AsignacionRequest;
import pe.unir.tfm.srp.seguimiento.dto.request.EliminacionRequest;
import pe.unir.tfm.srp.seguimiento.dto.response.AsignacionResponse;
import pe.unir.tfm.srp.seguimiento.service.AsignacionService;

@RestController
@RequestMapping("/api/asignaciones")
@RequiredArgsConstructor
public class AsignacionController {

    private final AsignacionService asignacionService;

    @GetMapping
    public ResponseEntity<List<AsignacionResponse>> listar() {
        return ResponseEntity.ok(asignacionService.listar());
    }

    @GetMapping("/por-usuario/{usuarioId}")
    public ResponseEntity<List<AsignacionResponse>> listarPorUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(asignacionService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/por-tarea/{tareaId}")
    public ResponseEntity<List<AsignacionResponse>> listarPorTarea(@PathVariable UUID tareaId) {
        return ResponseEntity.ok(asignacionService.listarPorTarea(tareaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsignacionResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(asignacionService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE_AREA', 'GESTOR_PROYECTO')")
    public ResponseEntity<AsignacionResponse> crear(@Valid @RequestBody AsignacionRequest request) {
        return ResponseEntity.ok(asignacionService.crear(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE_AREA', 'GESTOR_PROYECTO')")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id, @Valid @RequestBody EliminacionRequest request) {
        asignacionService.eliminar(id, request);
        return ResponseEntity.noContent().build();
    }
}
