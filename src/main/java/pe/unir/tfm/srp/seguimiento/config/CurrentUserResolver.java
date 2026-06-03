package pe.unir.tfm.srp.seguimiento.config;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserResolver {

    public static final UUID SYSTEM_USER = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public UUID obtenerUsuarioActualId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Jwt jwt)) {
            return SYSTEM_USER;
        }
        String subject = jwt.getSubject();
        if (subject == null || subject.isBlank()) {
            return SYSTEM_USER;
        }
        try {
            return UUID.fromString(subject);
        } catch (IllegalArgumentException ex) {
            return SYSTEM_USER;
        }
    }
}
