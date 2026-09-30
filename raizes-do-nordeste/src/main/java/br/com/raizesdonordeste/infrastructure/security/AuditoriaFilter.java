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

        // EXECUTA PRIMEIRO A EXECUÇAO NORMALMENTE
        filterChain.doFilter(request, response);

        // USUÁRIO PADRÃO PARA REQUISIÇÃO SEM AUTENTICAÇÃO
        String usuario = "ANONIMO";

        // OBTÉM A AUTENTICAÇÃO DIRETO DO SPRING SECURITY
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

        // IDENTIFICA A ROTA ACESSADA
        String rota = request.getRequestURI();

        // NÃO REGISTRA RECURSOS NO SWAGGER
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