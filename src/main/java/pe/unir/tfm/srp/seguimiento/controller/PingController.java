package pe.unir.tfm.srp.seguimiento.controller;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ping")
public class PingController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> ping() {
        return ResponseEntity.ok(Map.of(
                "servicio", "ms-seguimiento",
                "estado", "UP",
                "timestamp", Instant.now().toString()
        ));
    }

    @GetMapping("/secure")
    public ResponseEntity<Map<String, Object>> pingSecure(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(Map.of(
                "servicio", "ms-seguimiento",
                "estado", "AUTHENTICATED",
                "subject", jwt.getSubject(),
                "rol", jwt.getClaimAsString("rol"),
                "email", jwt.getClaimAsString("email"),
                "timestamp", Instant.now().toString()
        ));
    }
}
