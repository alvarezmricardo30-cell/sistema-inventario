package com.sena.sistemainventario.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sena.sistemainventario.model.Usuario;
import com.sena.sistemainventario.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario registrar(String username, String password, String nombre, String email) {
        if (repository.existsByUsername(username)) {
            throw new RuntimeException("El usuario ya existe");
        }
        Usuario usuario = new Usuario(username, hashPassword(password), nombre, email);
        return repository.save(usuario);
    }

    public Optional<Usuario> login(String username, String password) {
        return repository.findByUsername(username)
                .filter(u -> u.getPassword().equals(hashPassword(password)));
    }

    public Optional<Usuario> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Usuario> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al cifrar la contraseña", e);
        }
    }
}
