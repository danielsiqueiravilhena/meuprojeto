package br.com.raizesdonordeste.api.controller;

import br.com.raizesdonordeste.api.dto.LoginRequest;
import br.com.raizesdonordeste.api.dto.LoginResponse;
import br.com.raizesdonordeste.domain.entity.Usuario;
import br.com.raizesdonordeste.domain.repository.UsuarioRepository;
import br.com.raizesdonordeste.infrastructure.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        Usuario usuario = usuarioRepository
                .findByEmail(request.getEmail())
                .orElse(null);

        if (usuario == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("E-mail ou senha inválidos.");
        }

        if (!passwordEncoder.matches(
                request.getSenha(),
                usuario.getSenha())) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("E-mail ou senha inválidos.");
        }

        if (!usuario.getAtivo()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Usuário inativo.");
        }

        String token = jwtService.gerarToken(
                usuario.getEmail(),
                usuario.getRole()
        );

        LoginResponse response = new LoginResponse(
                token,
                "Bearer",
                usuario.getRole()
        );

        return ResponseEntity.ok(response);
    }
}