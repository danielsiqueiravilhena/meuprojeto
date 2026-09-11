package br.com.raizesdonordeste.api.controller;

import br.com.raizesdonordeste.domain.entity.Usuario;
import br.com.raizesdonordeste.domain.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Usuario usuario) {

        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("E-mail é obrigatório.");
        }

        if (usuario.getSenha() == null || usuario.getSenha().isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("Senha é obrigatória.");
        }

        if (usuario.getRole() == null || usuario.getRole().isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("Role é obrigatória.");
        }

        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Já existe um usuário com esse e-mail.");
        }

        usuario.setSenha(
                passwordEncoder.encode(usuario.getSenha())
        );

        Usuario novoUsuario = usuarioRepository.save(usuario);

        novoUsuario.setSenha(null);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(novoUsuario);
    }
}