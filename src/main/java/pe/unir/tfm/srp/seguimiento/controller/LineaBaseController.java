package pe.unir.tfm.srp.seguimiento.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.seguimiento.dto.request.CongelarLineaBaseRequest;
import pe.unir.tfm.srp.seguimiento.dto.response.LineaBaseResponse;
import pe.unir.tfm.srp.seguimiento.model.LineaBase;
import pe.unir.tfm.srp.seguimiento.service.LineaBaseService;

@RestController
@RequestMapping("/api/linea-base")
@RequiredArgsConstructor
public class LineaBaseController {

    private final LineaBaseService lineaBaseService;

    @GetMapping("/por-proyecto/{proyectoId}")
    public ResponseEntity<List<LineaBase>> listarPorProyecto(@PathVariable UUID proyectoId) {
        return ResponseEntity.ok(lineaBaseService.listarPorProyecto(proyectoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineaBaseResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(lineaBaseService.buscarPorId(id));
    }

    @PostMapping("/congelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE_AREA', 'GESTOR_PROYECTO')")
    public ResponseEntity<LineaBaseResponse> congelar(@Valid @RequestBody CongelarLineaBaseRequest request) {
        return ResponseEntity.ok(lineaBaseService.congelar(request));
    }
}
