package pe.unir.tfm.srp.seguimiento.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-proyectos")
public interface ProyectosClient {

    @GetMapping("/api/tareas/{id}")
    TareaProyectoResponse obtenerTarea(@PathVariable("id") UUID id);

    @PutMapping("/api/tareas/{id}/fechas")
    void actualizarFechas(@PathVariable("id") UUID id, @RequestBody TareaFechasRequest request);
}
