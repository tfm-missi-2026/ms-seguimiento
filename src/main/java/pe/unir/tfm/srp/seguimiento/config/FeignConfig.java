package pe.unir.tfm.srp.seguimiento.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor reenviarToken() {
        return plantilla -> {
            if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes atributos) {
                String token = atributos.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
                if (token != null) {
                    plantilla.header(HttpHeaders.AUTHORIZATION, token);
                }
            }
        };
    }
}
