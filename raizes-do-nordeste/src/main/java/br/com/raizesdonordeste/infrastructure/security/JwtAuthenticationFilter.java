package br.com.raizesdonordeste.infrastructure.security;

import br.com.raizesdonordeste.domain.entity.Usuario;
import br.com.raizesdonordeste.domain.repository.UsuarioRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UsuarioRepository usuarioRepository) {

        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        // Se não houver token, continua a requisição normalmente
        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // Remove "Bearer " e pega somente o token
        String token =
                authorizationHeader.substring(7);

        // Verifica se o token é válido
        if (!jwtService.tokenValido(token)) {

            filterChain.doFilter(request, response);
            return;
        }

        String email;

        try {

            email = jwtService.extrairEmail(token);

        } catch (Exception e) {

            filterChain.doFilter(request, response);
            return;
        }

        // Procura o usuário no banco pelo e-mail
        Usuario usuario =
                usuarioRepository
                        .findByEmail(email)
                        .orElse(null);

        // Usuário inexistente ou inativo
        if (usuario == null ||
                !usuario.getAtivo()) {

            filterChain.doFilter(request, response);
            return;
        }

        String role;

        try {

            role = jwtService.extrairRole(token);

        } catch (Exception e) {

            filterChain.doFilter(request, response);
            return;
        }

        // Token sem role
        if (role == null ||
                role.isBlank()) {

            filterChain.doFilter(request, response);
            return;
        }

        // Cria a autoridade do usuário
        var authorities = List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + role
                )
        );

        /*
         * IMPORTANTE:
         *
         * Usamos o e-mail como principal,
         * e não o objeto Usuario.
         *
         * Isso permite que a auditoria registre:
         *
         * admin@raizes.com
         *
         * em vez de:
         *
         * Usuario@540866ea
         */
        var authentication =
                new UsernamePasswordAuthenticationToken(
                        usuario.getEmail(),
                        null,
                        authorities
                );

        // Coloca a autenticação no contexto do Spring Security
        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        // Continua a requisição
        filterChain.doFilter(
                request,
                response
        );
    }
}