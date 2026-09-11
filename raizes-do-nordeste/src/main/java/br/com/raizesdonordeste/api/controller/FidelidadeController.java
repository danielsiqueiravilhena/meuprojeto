package br.com.raizesdonordeste.api.controller;

import br.com.raizesdonordeste.application.service.FidelidadeService;
import br.com.raizesdonordeste.domain.entity.Fidelidade;
import br.com.raizesdonordeste.domain.entity.HistoricoFidelidade;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fidelidade")
@Tag(
    name = "Fidelidade",
    description = "Consulta e gerenciamento de pontos de fidelidade"
)
public class FidelidadeController {

    private final FidelidadeService fidelidadeService;

    public FidelidadeController(FidelidadeService fidelidadeService) {
        this.fidelidadeService = fidelidadeService;
    }

    // =====================================================
    // CONSULTAR SALDO DE PONTOS
    // =====================================================

    @Operation(
        summary = "Consultar pontos do cliente",
        description = "Consulta o saldo de pontos de fidelidade de um cliente."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Fidelidade consultada com sucesso"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token ausente ou inválido"
        )
    })
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Fidelidade> consultar(
            @Parameter(
                description = "ID do cliente",
                example = "123"
            )
            @PathVariable Long clienteId) {

        return ResponseEntity.ok(
            fidelidadeService.consultar(clienteId)
        );
    }

    // =====================================================
    // REGISTRAR CONSENTIMENTO LGPD
    // =====================================================

    @Operation(
        summary = "Registrar consentimento LGPD",
        description =
            "Registra se o cliente autorizou sua participação "
            + "no programa de fidelidade."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Consentimento registrado com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Consentimento não informado"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token ausente ou inválido"
        )
    })
    @PutMapping("/cliente/{clienteId}/consentimento")
    public ResponseEntity<Fidelidade> registrarConsentimento(

            @Parameter(
                description = "ID do cliente",
                example = "123"
            )
            @PathVariable Long clienteId,

            @Parameter(
                description = "Indica se o cliente autorizou a participação na fidelidade",
                example = "true"
            )
            @RequestParam Boolean consentimento) {

        return ResponseEntity.ok(
            fidelidadeService.registrarConsentimento(
                clienteId,
                consentimento
            )
        );
    }

    // =====================================================
    // ADICIONAR PONTOS
    // =====================================================

    @Operation(
        summary = "Adicionar pontos",
        description =
            "Adiciona pontos ao saldo de fidelidade do cliente "
            + "e registra a movimentação no histórico."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Pontos adicionados com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Quantidade de pontos inválida"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token ausente ou inválido"
        )
    })
    @PostMapping("/cliente/{clienteId}/pontos")
    public ResponseEntity<Fidelidade> adicionarPontos(

            @Parameter(
                description = "ID do cliente",
                example = "123"
            )
            @PathVariable Long clienteId,

            @Parameter(
                description = "Quantidade de pontos a adicionar",
                example = "50"
            )
            @RequestParam Integer pontos) {

        return ResponseEntity.ok(
            fidelidadeService.adicionarPontos(
                clienteId,
                pontos
            )
        );
    }

    // =====================================================
    // RESGATAR PONTOS
    // =====================================================

    @Operation(
        summary = "Resgatar pontos",
        description =
            "Resgata pontos do saldo do cliente, desde que "
            + "exista consentimento LGPD e saldo suficiente. "
            + "O resgate também é registrado no histórico."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Pontos resgatados com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Quantidade inválida ou ausência de consentimento LGPD"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token ausente ou inválido"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Saldo de pontos insuficiente"
        )
    })
    @PostMapping("/cliente/{clienteId}/resgate")
    public ResponseEntity<Fidelidade> resgatarPontos(

            @Parameter(
                description = "ID do cliente",
                example = "123"
            )
            @PathVariable Long clienteId,

            @Parameter(
                description = "Quantidade de pontos a resgatar",
                example = "20"
            )
            @RequestParam Integer pontos) {

        return ResponseEntity.ok(
            fidelidadeService.resgatarPontos(
                clienteId,
                pontos
            )
        );
    }

    // =====================================================
    // CONSULTAR HISTÓRICO
    // =====================================================

    @Operation(
        summary = "Consultar histórico de pontos",
        description =
            "Consulta o histórico de movimentações de pontos "
            + "de fidelidade de um cliente."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Histórico consultado com sucesso"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token ausente ou inválido"
        )
    })
    @GetMapping("/cliente/{clienteId}/historico")
    public ResponseEntity<List<HistoricoFidelidade>> consultarHistorico(

            @Parameter(
                description = "ID do cliente",
                example = "123"
            )
            @PathVariable Long clienteId) {

        return ResponseEntity.ok(
            fidelidadeService.consultarHistorico(clienteId)
        );
    }

    // =====================================================
    // REGISTRAR HISTÓRICO SEM ALTERAR O SALDO
    // =====================================================

    @Operation(
        summary = "Registrar histórico de pontos",
        description =
            "Registra uma movimentação no histórico sem alterar "
            + "o saldo atual de pontos."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Histórico registrado com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Quantidade de pontos inválida"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token ausente ou inválido"
        )
    })
    @PostMapping("/cliente/{clienteId}/historico")
    public ResponseEntity<HistoricoFidelidade> registrarHistorico(

            @Parameter(
                description = "ID do cliente",
                example = "123"
            )
            @PathVariable Long clienteId,

            @Parameter(
                description = "Quantidade de pontos registrada no histórico",
                example = "37"
            )
            @RequestParam Integer pontos,

            @Parameter(
                description = "Motivo da movimentação",
                example = "Pagamento aprovado - pedido 3"
            )
            @RequestParam String motivo) {

        return ResponseEntity.ok(
            fidelidadeService.registrarHistorico(
                clienteId,
                pontos,
                motivo
            )
        );
    }

    // =====================================================
    // EXCLUIR REGISTRO DO HISTÓRICO
    // =====================================================

    @Operation(
        summary = "Excluir registro do histórico",
        description =
            "Exclui um registro específico do histórico de "
            + "fidelidade sem alterar o saldo de pontos."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Registro excluído com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Registro não encontrado"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token ausente ou inválido"
        )
    })
    @DeleteMapping("/historico/{id}")
    public ResponseEntity<Void> excluirHistorico(
            @Parameter(
                description = "ID do registro do histórico",
                example = "1"
            )
            @PathVariable Long id) {

        fidelidadeService.excluirHistorico(id);

        return ResponseEntity.noContent().build();
    }
}