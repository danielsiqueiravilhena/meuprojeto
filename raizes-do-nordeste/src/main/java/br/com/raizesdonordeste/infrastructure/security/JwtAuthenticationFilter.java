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

        // SE NÃO HOUVER TOKEN CONTINUA COM A REQUISIÇÃO NORMALMENTE
        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // REMOVE BEARER E PEGA SOMENTE O TOKEN
        String token =
                authorizationHeader.substring(7);

        // VERIFICA SE O TOKEN É VALIDO
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

        // PROCURA O USUÁRIO NO BANCO PELO EMAIL
        Usuario usuario =
                usuarioRepository
                        .findByEmail(email)
                        .orElse(null);

        // USUÁRIO INEXISTENTE OU INATIVO
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

        // TOKEN SEM ROLE
        if (role == null ||
                role.isBlank()) {

            filterChain.doFilter(request, response);
            return;
        }

        // CRIA A AUTORIDADE DO USUÁRIO
        var authorities = List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + role
                )
        );

        /*
         * IMPORTANTE:
         *
         * USAMOS O EMAIL COMO PRINCIPAL
         * ISSO PERMITE QUE AUDITORIA REGISTRE:
         *
         * admin@raizes.com
         *
         * EM VEZ DE:
         *
         * Usuario@algumacoisa
         */
        var authentication =
                new UsernamePasswordAuthenticationToken(
                        usuario.getEmail(),
                        null,
                        authorities
                );

        // COLOCA A AUTENTICAÇÃO NO CONTEXTO SPRING SECURITY
        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        // CONTINUA A REQUISIÇÃO
        filterChain.doFilter(
                request,
                response
        );
    }
}