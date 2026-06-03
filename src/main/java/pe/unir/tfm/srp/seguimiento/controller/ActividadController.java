package pe.unir.tfm.srp.seguimiento.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.seguimiento.dto.request.ActividadRequest;
import pe.unir.tfm.srp.seguimiento.dto.request.EliminacionRequest;
import pe.unir.tfm.srp.seguimiento.dto.response.ActividadResponse;
import pe.unir.tfm.srp.seguimiento.service.ActividadService;

@RestController
@RequestMapping("/api/actividades")
@RequiredArgsConstructor
public class ActividadController {

    private final ActividadService actividadService;

    @GetMapping
    public ResponseEntity<List<ActividadResponse>> listar() {
        return ResponseEntity.ok(actividadService.listar());
    }

    @GetMapping("/por-fecha")
    public ResponseEntity<List<ActividadResponse>> listarPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(actividadService.listarPorFecha(fecha));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActividadResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(actividadService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ActividadResponse> crear(@Valid @RequestBody ActividadRequest request) {
        return ResponseEntity.ok(actividadService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActividadResponse> actualizar(@PathVariable UUID id,
                                                        @Valid @RequestBody ActividadRequest request) {
        return ResponseEntity.ok(actividadService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id, @Valid @RequestBody EliminacionRequest request) {
        actividadService.eliminar(id, request);
        return ResponseEntity.noContent().build();
    }
}
