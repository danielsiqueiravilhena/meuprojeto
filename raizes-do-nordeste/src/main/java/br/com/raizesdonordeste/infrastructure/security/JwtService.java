package br.com.raizesdonordeste.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${JWT_SECRET}")
    private String secret;

    private SecretKey getChave() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String gerarToken(String email, String role) {

        Date agora = new Date();

        Date expiracao = new Date(
                agora.getTime() + 3600000
        );

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(getChave())
                .compact();
    }

    public Claims extrairClaims(String token) {

        return Jwts.parser()
                .verifyWith(getChave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    public String extrairRole(String token) {
        return extrairClaims(token)
                .get("role", String.class);
    }

    public boolean tokenValido(String token) {

        try {
            Claims claims = extrairClaims(token);

            Date expiracao = claims.getExpiration();

            return expiracao != null &&
                   expiracao.after(new Date());

        } catch (Exception e) {
            return false;
        }
    }
}