package com.sena.sistemainventario.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sena.sistemainventario.model.Usuario;
import com.sena.sistemainventario.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@RestController
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpSession session) {
        String username = body.get("username");
        String password = body.get("password");

        Optional<Usuario> usuario = usuarioService.login(username, password);

        if (usuario.isPresent()) {
            session.setAttribute("usuarioId", usuario.get().getId());
            session.setAttribute("usuarioNombre", usuario.get().getNombre());
            session.setAttribute("usuarioRol", usuario.get().getRol());
            return ResponseEntity.ok(Map.of(
                "mensaje", "Login exitoso",
                "nombre", usuario.get().getNombre(),
                "rol", usuario.get().getRol(),
                "usuarioId", usuario.get().getId()
            ));
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Usuario o contraseña incorrectos"));
        }
    }

    @PostMapping("/api/registro")
    public ResponseEntity<?> registro(@RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String password = body.get("password");
            String nombre = body.get("nombre");
            String email = body.get("email");

            if (username == null || password == null || nombre == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Todos los campos son obligatorios"));
            }

            Usuario usuario = usuarioService.registrar(username, password, nombre, email);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Usuario registrado correctamente",
                "usuarioId", usuario.getId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/sesion")
    public ResponseEntity<?> verificarSesion(HttpSession session) {
        if (session.getAttribute("usuarioId") != null) {
            return ResponseEntity.ok(Map.of(
                "autenticado", true,
                "nombre", session.getAttribute("usuarioNombre"),
                "rol", session.getAttribute("usuarioRol")
            ));
        } else {
            return ResponseEntity.ok(Map.of("autenticado", false));
        }
    }

    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("mensaje", "Sesion cerrada"));
    }
}
