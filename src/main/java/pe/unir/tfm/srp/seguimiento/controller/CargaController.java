package pe.unir.tfm.srp.seguimiento.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.seguimiento.config.CurrentUserResolver;
import pe.unir.tfm.srp.seguimiento.dto.response.CargaRecursoResponse;
import pe.unir.tfm.srp.seguimiento.service.CargaService;

@RestController
@RequestMapping("/api/carga")
@RequiredArgsConstructor
public class CargaController {

    private static final Set<String> ROLES_PRIVILEGIADOS =
            Set.of("ROLE_ADMIN", "ROLE_JEFE_AREA", "ROLE_GESTOR_PROYECTO");

    private final CargaService cargaService;
    private final CurrentUserResolver currentUserResolver;

    @GetMapping("/equipo")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE_AREA', 'GESTOR_PROYECTO')")
    public ResponseEntity<List<CargaRecursoResponse>> cargaEquipo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) List<UUID> tareaIds) {
        return ResponseEntity.ok(cargaService.cargaEquipo(desde, hasta, tareaIds));
    }

    @GetMapping("/por-usuario/{usuarioId}")
    public ResponseEntity<CargaRecursoResponse> cargaPorUsuario(
            @PathVariable UUID usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) List<UUID> tareaIds,
            Authentication authentication) {
        validarAccesoAUsuario(usuarioId, authentication);
        return ResponseEntity.ok(cargaService.cargaPorUsuario(usuarioId, desde, hasta, tareaIds));
    }

    private void validarAccesoAUsuario(UUID usuarioId, Authentication authentication) {
        boolean privilegiado = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(ROLES_PRIVILEGIADOS::contains);
        if (!privilegiado && !usuarioId.equals(currentUserResolver.obtenerUsuarioActualId())) {
            throw new AccessDeniedException("No tiene permiso para consultar la carga de otro recurso");
        }
    }
}
