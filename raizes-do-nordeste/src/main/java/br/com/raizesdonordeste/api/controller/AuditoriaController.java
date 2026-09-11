package br.com.raizesdonordeste.api.controller;

import br.com.raizesdonordeste.domain.entity.Auditoria;
import br.com.raizesdonordeste.domain.repository.AuditoriaRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auditoria")
@Tag(
        name = "Auditoria",
        description = "Consulta dos registros de auditoria do sistema"
)
public class AuditoriaController {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaController(
            AuditoriaRepository auditoriaRepository) {

        this.auditoriaRepository = auditoriaRepository;
    }

    @Operation(
            summary = "Listar auditorias",
            description =
                    "Lista os registros de auditoria das ações realizadas no sistema."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Registros de auditoria encontrados"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente ou inválido"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário sem permissão"
            )
    })
    @GetMapping
    public ResponseEntity<List<Auditoria>> listar() {

        return ResponseEntity.ok(
                auditoriaRepository.findAll()
        );
    }
}