package pe.unir.tfm.srp.seguimiento.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.seguimiento.dto.request.EliminacionRequest;
import pe.unir.tfm.srp.seguimiento.dto.request.VariacionCrearRequest;
import pe.unir.tfm.srp.seguimiento.dto.request.VariacionResolverRequest;
import pe.unir.tfm.srp.seguimiento.dto.response.VariacionResponse;
import pe.unir.tfm.srp.seguimiento.service.VariacionService;

@RestController
@RequestMapping("/api/variaciones")
@RequiredArgsConstructor
public class VariacionController {

    private final VariacionService variacionService;

    @GetMapping
    public ResponseEntity<List<VariacionResponse>> listar() {
        return ResponseEntity.ok(variacionService.listar());
    }

    @GetMapping("/por-tarea/{tareaId}")
    public ResponseEntity<List<VariacionResponse>> listarPorTarea(@PathVariable UUID tareaId) {
        return ResponseEntity.ok(variacionService.listarPorTarea(tareaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VariacionResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(variacionService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<VariacionResponse> reportar(@Valid @RequestBody VariacionCrearRequest request) {
        return ResponseEntity.ok(variacionService.crear(request));
    }

    @PutMapping("/{id}/resolver")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE_AREA', 'GESTOR_PROYECTO')")
    public ResponseEntity<VariacionResponse> resolver(@PathVariable UUID id,
                                                      @Valid @RequestBody VariacionResolverRequest request) {
        return ResponseEntity.ok(variacionService.resolver(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE_AREA', 'GESTOR_PROYECTO')")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id, @Valid @RequestBody EliminacionRequest request) {
        variacionService.eliminar(id, request);
        return ResponseEntity.noContent().build();
    }
}
