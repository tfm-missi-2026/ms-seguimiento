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
import pe.unir.tfm.srp.seguimiento.config.CurrentUserResolver;
import pe.unir.tfm.srp.seguimiento.dto.request.BitacoraRequest;
import pe.unir.tfm.srp.seguimiento.dto.request.EliminacionRequest;
import pe.unir.tfm.srp.seguimiento.dto.response.BitacoraResponse;
import pe.unir.tfm.srp.seguimiento.service.BitacoraService;

@RestController
@RequestMapping("/api/bitacora")
@RequiredArgsConstructor
public class BitacoraController {

    private final BitacoraService bitacoraService;
    private final CurrentUserResolver currentUserResolver;

    @GetMapping("/mia")
    public ResponseEntity<List<BitacoraResponse>> miBitacoraDelDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(
                bitacoraService.listarPorUsuarioYFecha(currentUserResolver.obtenerUsuarioActualId(), fecha));
    }

    @GetMapping("/mia/rango")
    public ResponseEntity<List<BitacoraResponse>> miBitacoraEnRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(
                bitacoraService.listarPorUsuarioYRango(currentUserResolver.obtenerUsuarioActualId(), desde, hasta));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BitacoraResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(bitacoraService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<BitacoraResponse> crear(@Valid @RequestBody BitacoraRequest request) {
        return ResponseEntity.ok(bitacoraService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BitacoraResponse> actualizar(@PathVariable UUID id,
                                                       @Valid @RequestBody BitacoraRequest request) {
        return ResponseEntity.ok(bitacoraService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id, @Valid @RequestBody EliminacionRequest request) {
        bitacoraService.eliminar(id, request);
        return ResponseEntity.noContent().build();
    }
}
