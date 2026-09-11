package br.com.raizesdonordeste.infrastructure.security;

import br.com.raizesdonordeste.application.service.AuditoriaService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuditoriaFilter extends OncePerRequestFilter {

    private final AuditoriaService auditoriaService;

    public AuditoriaFilter(
            AuditoriaService auditoriaService) {

        this.auditoriaService = auditoriaService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Executa primeiro a requisição normalmente
        filterChain.doFilter(request, response);

        // Usuário padrão para requisições sem autenticação
        String usuario = "ANONIMO";

        // Obtém a autenticação diretamente do Spring Security
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getName() != null
                && !authentication.getName().equals("anonymousUser")) {

            usuario = authentication.getName();
        }

        // Identifica a rota acessada
        String rota = request.getRequestURI();

        // Não registra recursos do Swagger
        if (!rota.startsWith("/swagger-ui")
                && !rota.startsWith("/v3/api-docs")
                && !rota.equals("/favicon.ico")) {

            auditoriaService.registrar(
                    usuario,
                    request.getMethod(),
                    rota,
                    response.getStatus()
            );
        }
    }
}