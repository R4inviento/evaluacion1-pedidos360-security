package cl.duoc.pedidos360.bff.controller;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public Map<String, String> publicEndpoint() {

        return Map.of(
            "mensaje",
            "Endpoint publico funcionando"
        );
    }

    @GetMapping("/private")
    public Map<String, String> privateEndpoint() {

        return Map.of(
            "mensaje",
            "JWT valido - acceso autorizado"
        );
    }

    @GetMapping("/scope")
    @PreAuthorize(
        "hasAuthority('SCOPE_OT.create')"
    )
    public Map<String, String> scopeEndpoint() {

        return Map.of(
            "mensaje",
            "Scope OT.create validado correctamente"
        );
    }

    @GetMapping("/admin")
    @PreAuthorize(
        "hasRole('Admin')"
    )
    public Map<String, String> adminEndpoint(
            Authentication authentication) {

        return Map.of(
            "mensaje",
            "Acceso de administrador autorizado",
            "usuario",
            authentication.getName(),
            "rol",
            "Admin"
        );
    }
}