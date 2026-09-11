package br.com.raizesdonordeste.api.controller;

import br.com.raizesdonordeste.application.service.FidelidadeService;
import br.com.raizesdonordeste.application.service.PedidoService;
import br.com.raizesdonordeste.domain.entity.Pagamento;
import br.com.raizesdonordeste.domain.entity.Pedido;
import br.com.raizesdonordeste.domain.enums.FormaPagamento;
import br.com.raizesdonordeste.domain.enums.StatusPedido;
import br.com.raizesdonordeste.domain.repository.PagamentoRepository;
import br.com.raizesdonordeste.domain.repository.PedidoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;


@RestController
@RequestMapping("/pagamentos")
@Tag(
        name = "Pagamentos",
        description = "Processamento de pagamentos MOCK dos pedidos"
)
public class PagamentoController {


    private final PagamentoRepository pagamentoRepository;

    private final PedidoRepository pedidoRepository;

    private final FidelidadeService fidelidadeService;

    private final PedidoService pedidoService;



    public PagamentoController(
            PagamentoRepository pagamentoRepository,
            PedidoRepository pedidoRepository,
            FidelidadeService fidelidadeService,
            PedidoService pedidoService) {

        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
        this.fidelidadeService = fidelidadeService;
        this.pedidoService = pedidoService;

    }



    @Operation(
            summary = "Processar pagamento MOCK",
            description =
                    "Processa pagamento simulado. "
                    + "Resultado aceito: APROVADO ou RECUSADO."
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Pagamento processado"
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou pagamento já processado"
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Pedido não encontrado"
            )
    })



    @PostMapping("/{pedidoId}")
    @Transactional
    public ResponseEntity<?> processarPagamento(


            @Parameter(
                    description = "ID do pedido",
                    example = "27"
            )
            @PathVariable Long pedidoId,


            @Parameter(
                    description = "Resultado do pagamento",
                    example = "APROVADO"
            )
            @RequestParam String resultado

    ) {


        Pedido pedido = pedidoRepository
                .findById(pedidoId)
                .orElse(null);



        if (pedido == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "error",
                                    "NOT_FOUND",
                                    "message",
                                    "Pedido não encontrado."
                            )
                    );
        }



        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    "BAD_REQUEST",
                                    "message",
                                    "O pedido não está aguardando pagamento."
                            )
                    );
        }



        if (pagamentoRepository
                .findByPedidoId(pedidoId)
                .isPresent()) {


            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    "BAD_REQUEST",
                                    "message",
                                    "Este pedido já foi processado."
                            )
                    );
        }




        if (resultado == null ||
                (!resultado.equalsIgnoreCase("APROVADO")
                &&
                !resultado.equalsIgnoreCase("RECUSADO"))) {


            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    "BAD_REQUEST",
                                    "message",
                                    "O resultado deve ser APROVADO ou RECUSADO."
                            )
                    );
        }



        String resultadoFinal =
                resultado.toUpperCase();



        String statusPagamento =
                resultadoFinal.equals("APROVADO")
                        ? "APROVADO"
                        : "RECUSADO";



        Pagamento pagamento = new Pagamento(

                pedido,

                FormaPagamento.MOCK,

                pedido.getValorTotal(),

                statusPagamento

        );


        pagamento.setDataPagamento(
                LocalDateTime.now()
        );



        pagamentoRepository.save(pagamento);




        int pontosGerados = 0;



        if (resultadoFinal.equals("APROVADO")) {


            pedido.setStatus(
                    StatusPedido.PAGAMENTO_APROVADO
            );


            pontosGerados =
                    pedido.getValorTotal()
                            .intValue();



            fidelidadeService.adicionarPontos(
                    pedido.getClienteId(),
                    pontosGerados
            );


        } else {


            pedido.setStatus(
                    StatusPedido.CANCELADO
            );


            pedidoService.devolverEstoque(pedido);

        }



        pedidoRepository.save(pedido);




        String mensagem;



        if (resultadoFinal.equals("APROVADO")) {

            mensagem =
                    "Pagamento aprovado com sucesso.";

        } else {

            mensagem =
                    "Pagamento recusado. Pedido cancelado.";

        }



        return ResponseEntity.ok(

                Map.of(

                        "pedidoId",
                        pedido.getId(),

                        "statusPedido",
                        pedido.getStatus(),

                        "pontosGerados",
                        pontosGerados,

                        "pagamentoId",
                        pagamento.getId(),

                        "message",
                        mensagem

                )

        );

    }

}