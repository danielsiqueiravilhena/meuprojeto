package br.com.raizesdonordeste.infrastructure.security;

import br.com.raizesdonordeste.application.service.AuditoriaService;
import br.com.raizesdonordeste.domain.entity.Usuario;
import br.com.raizesdonordeste.domain.repository.UsuarioRepository;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;

    public SecurityConfig(
            JwtService jwtService,
            UsuarioRepository usuarioRepository,
            AuditoriaService auditoriaService) {

        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
    }

    // SENHAS

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // USUÁRIOS DO BANCO

    @Bean
    public UserDetailsService userDetailsService() {

        return email -> {

            Usuario usuario = usuarioRepository
                    .findByEmail(email)
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "Usuário não encontrado."
                            )
                    );

            return User.withUsername(usuario.getEmail())
                    .password(usuario.getSenha())
                    .roles(usuario.getRole())
                    .disabled(!Boolean.TRUE.equals(usuario.getAtivo()))
                    .build();
        };
    }

    // FILTRO JWT

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {

        return new JwtAuthenticationFilter(
                jwtService,
                usuarioRepository
        );
    }

    // FILTRO DE AUDITORIA

    @Bean
    public AuditoriaFilter auditoriaFilter() {

        return new AuditoriaFilter(
                auditoriaService
        );
    }

    // CONFIGURAÇÃO DE SEGURANÇA

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuditoriaFilter auditoriaFilter)
            throws Exception {

        http

            // API REST

            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            .formLogin(form -> form.disable())

            .httpBasic(basic -> basic.disable())

            // TRATAMENTO DE ERROS

            .exceptionHandling(exceptions -> exceptions

                    // 401 - NÃO AUTENTICADO

                    .authenticationEntryPoint(
                            (request, response, authException) -> {

                                response.setStatus(
                                        HttpServletResponse.SC_UNAUTHORIZED
                                );

                                response.setContentType(
                                        "application/json"
                                );

                                response.getWriter().write(
                                        "{\"error\":\"UNAUTHORIZED\","
                                        + "\"message\":\"Token de autenticação ausente ou inválido.\"}"
                                );
                            }
                    )

                    // 403 - SEM PERMISSÃO

                    .accessDeniedHandler(
                            (request, response, accessDeniedException) -> {

                                response.setStatus(
                                        HttpServletResponse.SC_FORBIDDEN
                                );

                                response.setContentType(
                                        "application/json"
                                );

                                response.getWriter().write(
                                        "{\"error\":\"FORBIDDEN\","
                                        + "\"message\":\"Usuário autenticado, mas sem permissão para acessar este recurso.\"}"
                                );
                            }
                    )
            )

            // AUTORIZAÇÃO DAS ROTAS

            .authorizeHttpRequests(auth -> auth

                    // ROTAS PÚBLICAS

                    .requestMatchers("/auth/**")
                    .permitAll()

                    .requestMatchers("/error")
                    .permitAll()

                    // SWAGGER / OPENAPI

                    .requestMatchers(
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/v3/api-docs/**"
                    )
                    .permitAll()

                    // USUÁRIOS
                    // SOMENTE ADMIN

                    .requestMatchers("/usuarios/**")
                    .hasRole("ADMIN")

                    // PRODUTOS
                    // USUÁRIO AUTENTICADO

                    .requestMatchers("/produtos/**")
                    .authenticated()

                    // ESTOQUE
                    // SOMENTE ADMIN

                    .requestMatchers("/estoque/**")
                    .hasRole("ADMIN")

                    // PEDIDOS
                    // USUÁRIO AUTENTICADO

                    .requestMatchers("/pedidos/**")
                    .authenticated()

                    // PAGAMENTOS
                    // USUÁRIO AUTENTICADO

                    .requestMatchers("/pagamentos/**")
                    .authenticated()

                    // FIDELIDADE
                    // USUÁRIO AUTENTICADO

                    .requestMatchers("/fidelidade/**")
                    .authenticated()

                    // UNIDADES
                    // USUÁRIO AUTENTICADO

                    .requestMatchers("/unidades/**")
                    .authenticated()

                    // AUDITORIA
                    // SOMENTE ADMIN

                    .requestMatchers("/auditoria/**")
                    .hasRole("ADMIN")

                    // QUALQUER OUTRA ROTA

                    .anyRequest()
                    .authenticated()
            )

            // FILTROS

            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            )

            .addFilterAfter(
                    auditoriaFilter,
                    JwtAuthenticationFilter.class
            );

        return http.build();
    }
}