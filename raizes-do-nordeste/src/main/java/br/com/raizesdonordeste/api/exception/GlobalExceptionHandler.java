package br.com.raizesdonordeste.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> tratarJsonInvalido(
            HttpMessageNotReadableException ex) {

        Map<String, Object> resposta = new HashMap<>();

        resposta.put("error", "BAD_REQUEST");
        resposta.put(
                "message",
                "Dados enviados na requisição são inválidos."
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(resposta);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> tratarErroDeStatus(
            ResponseStatusException ex) {

        Map<String, Object> resposta = new HashMap<>();

        HttpStatus status = HttpStatus.valueOf(
                ex.getStatusCode().value()
        );

        String error;

        if (status == HttpStatus.BAD_REQUEST) {
            error = "BAD_REQUEST";
        } else if (status == HttpStatus.NOT_FOUND) {
            error = "NOT_FOUND";
        } else if (status == HttpStatus.CONFLICT) {
            error = "CONFLICT";
        } else if (status == HttpStatus.UNAUTHORIZED) {
            error = "UNAUTHORIZED";
        } else if (status == HttpStatus.FORBIDDEN) {
            error = "FORBIDDEN";
        } else {
            error = status.name();
        }

        resposta.put("error", error);
        resposta.put(
                "message",
                ex.getReason() != null
                        ? ex.getReason()
                        : "Ocorreu um erro na requisição."
        );

        return ResponseEntity
                .status(status)
                .body(resposta);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> tratarArgumentoInvalido(
            IllegalArgumentException ex) {

        Map<String, Object> resposta = new HashMap<>();

        resposta.put("error", "BAD_REQUEST");
        resposta.put(
                "message",
                ex.getMessage() != null
                        ? ex.getMessage()
                        : "Dados informados são inválidos."
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(resposta);
    }
}