package cl.duoc.pedidos360.controller;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ot")
public class OrdenTrabajoController {

    @GetMapping("/estado")
    public Map<String, String> estado() {
        return Map.of(
            "mensaje", "Token válido. Acceso autorizado al backend.",
            "api", "Pedidos360 BFF Microservice"
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_OT.Create')")
    public Map<String, String> crearOt() {
        return Map.of(
            "mensaje", "Orden de trabajo creada correctamente.",
            "scopeRequerido", "OT.Create"
        );
    }
}
