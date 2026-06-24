package pe.unir.tfm.srp.seguimiento.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.seguimiento.dto.response.AvanceProyectoResponse;
import pe.unir.tfm.srp.seguimiento.service.AvanceService;

@RestController
@RequestMapping("/api/avance")
@RequiredArgsConstructor
public class AvanceController {

    private final AvanceService avanceService;

    @GetMapping("/por-proyecto/{proyectoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE_AREA', 'GESTOR_PROYECTO')")
    public ResponseEntity<AvanceProyectoResponse> avancePorProyecto(@PathVariable UUID proyectoId) {
        return ResponseEntity.ok(avanceService.avancePorProyecto(proyectoId));
    }
}
