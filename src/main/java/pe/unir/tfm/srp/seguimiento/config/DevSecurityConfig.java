package pe.unir.tfm.srp.seguimiento.config;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Perfil dev: deshabilita la exigencia de JWT para probar endpoints sin token desde IntelliJ.
// Inyecta un Jwt simulado (admin del seed + todos los roles) para que @PreAuthorize y
// CurrentUserResolver funcionen igual que con un token real. NO se activa en Docker/produccion.
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("dev")
public class DevSecurityConfig {

    private static final String DEV_USER_ID = "00000000-0000-0000-0000-000000000900";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a.anyRequest().permitAll())
                .addFilterBefore(devAuthFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private OncePerRequestFilter devAuthFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
                    throws ServletException, IOException {
                Jwt jwt = Jwt.withTokenValue("dev-token")
                        .header("alg", "none")
                        .subject(DEV_USER_ID)
                        .claim("rol", "ADMIN")
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(3600))
                        .build();
                JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("ROLE_JEFE_AREA"),
                        new SimpleGrantedAuthority("ROLE_GESTOR_PROYECTO"),
                        new SimpleGrantedAuthority("ROLE_RECURSO_TECNICO")));
                SecurityContextHolder.getContext().setAuthentication(auth);
                chain.doFilter(req, res);
            }
        };
    }
}
