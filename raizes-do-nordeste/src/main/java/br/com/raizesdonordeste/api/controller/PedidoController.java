package br.com.raizesdonordeste.api.controller;

import br.com.raizesdonordeste.api.dto.PedidoRequest;
import br.com.raizesdonordeste.application.service.PedidoService;
import br.com.raizesdonordeste.domain.entity.Pedido;
import br.com.raizesdonordeste.domain.enums.CanalPedido;
import br.com.raizesdonordeste.domain.enums.StatusPedido;
import br.com.raizesdonordeste.domain.repository.PedidoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/pedidos")
@Tag(
        name = "Pedidos",
        description = "Operações de pedidos"
)
public class PedidoController {


    private final PedidoRepository pedidoRepository;

    private final PedidoService pedidoService;



    public PedidoController(
            PedidoRepository pedidoRepository,
            PedidoService pedidoService) {

        this.pedidoRepository = pedidoRepository;
        this.pedidoService = pedidoService;

    }





    @GetMapping
    @Operation(
            summary = "Listar pedidos"
    )
    public ResponseEntity<List<Pedido>> listar(

            @RequestParam(required = false)
            CanalPedido canalPedido

    ) {


        if (canalPedido != null) {

            return ResponseEntity.ok(
                    pedidoRepository.findByCanalPedido(canalPedido)
            );

        }


        return ResponseEntity.ok(
                pedidoRepository.findAll()
        );

    }







    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar pedido por ID"
    )
    public ResponseEntity<?> buscarPorId(

            @PathVariable Long id

    ) {


        return pedidoRepository
                .findById(id)

                .map(ResponseEntity::ok)

                .orElse(
                        ResponseEntity.notFound().build()
                );

    }







    @PostMapping
    @Operation(
            summary = "Criar pedido"
    )
    public ResponseEntity<Pedido> criar(

            @RequestBody PedidoRequest pedidoRequest

    ) {


        Pedido pedido =
                pedidoService.criar(pedidoRequest);



        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pedido);

    }









    @PutMapping("/{id}/status")
    @Operation(
            summary = "Atualizar status do pedido"
    )
    public ResponseEntity<?> atualizarStatus(


            @PathVariable Long id,


            @RequestParam StatusPedido status

    ) {



        Pedido pedido = pedidoRepository
                .findById(id)
                .orElse(null);




        if (pedido == null) {


            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            "Pedido não encontrado."
                    );

        }






        if (!transicaoPermitida(
                pedido.getStatus(),
                status
        )) {



            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(

                            "Transição inválida: "
                                    + pedido.getStatus()
                                    + " -> "
                                    + status

                    );

        }






        pedido.setStatus(status);



        // DEVOLVE ESTOQUE QUANDO CANCELAR PEDIDO

        if (status == StatusPedido.CANCELADO) {

            pedidoService.devolverEstoque(pedido);

        }






        return ResponseEntity.ok(

                pedidoRepository.save(pedido)

        );

    }









    private boolean transicaoPermitida(

            StatusPedido atual,

            StatusPedido novo

    ) {



        if (novo == null) {

            return false;

        }





        if (novo == StatusPedido.CANCELADO) {


            return atual != StatusPedido.ENTREGUE

                    && atual != StatusPedido.CANCELADO;

        }






        return switch (atual) {


            case AGUARDANDO_PAGAMENTO ->

                    novo == StatusPedido.PAGAMENTO_APROVADO;



            case PAGAMENTO_APROVADO ->

                    novo == StatusPedido.EM_PREPARACAO;



            case EM_PREPARACAO ->

                    novo == StatusPedido.PRONTO;



            case PRONTO ->

                    novo == StatusPedido.ENTREGUE;



            case ENTREGUE, CANCELADO ->

                    false;

        };

    }


}