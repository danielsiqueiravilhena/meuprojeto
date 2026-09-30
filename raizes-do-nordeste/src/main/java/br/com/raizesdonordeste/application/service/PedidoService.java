package br.com.raizesdonordeste.application.service;

import br.com.raizesdonordeste.api.dto.ItemPedidoRequest;
import br.com.raizesdonordeste.api.dto.PedidoRequest;

import br.com.raizesdonordeste.domain.entity.Estoque;
import br.com.raizesdonordeste.domain.entity.ItemPedido;
import br.com.raizesdonordeste.domain.entity.Pedido;
import br.com.raizesdonordeste.domain.entity.Produto;
import br.com.raizesdonordeste.domain.entity.Unidade;

import br.com.raizesdonordeste.domain.repository.EstoqueRepository;
import br.com.raizesdonordeste.domain.repository.PedidoRepository;
import br.com.raizesdonordeste.domain.repository.ProdutoRepository;
import br.com.raizesdonordeste.domain.repository.UnidadeRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Service
public class PedidoService {


    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;
    private final UnidadeRepository unidadeRepository;



    public PedidoService(
            PedidoRepository pedidoRepository,
            ProdutoRepository produtoRepository,
            EstoqueRepository estoqueRepository,
            UnidadeRepository unidadeRepository) {


        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueRepository = estoqueRepository;
        this.unidadeRepository = unidadeRepository;
    }




    @Transactional
    public Pedido criar(PedidoRequest pedidoRequest) {


        if (pedidoRequest == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Os dados do pedido devem ser informados."
            );
        }



        if (pedidoRequest.getCanalPedido() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O canalPedido deve ser informado."
            );
        }



        if (pedidoRequest.getClienteId() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O clienteId deve ser informado."
            );
        }



        if (pedidoRequest.getUnidadeId() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O unidadeId deve ser informado."
            );
        }




        Unidade unidade =
                unidadeRepository.findById(
                        pedidoRequest.getUnidadeId()
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Unidade não encontrada."
                        )
                );




        if (!unidade.getAtiva()) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A unidade está inativa."
            );
        }





        if (pedidoRequest.getFormaPagamento() == null ||
                !pedidoRequest.getFormaPagamento()
                        .equalsIgnoreCase("MOCK")) {


            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Forma de pagamento deve ser MOCK."
            );
        }





        List<ItemPedidoRequest> itensRequest =
                pedidoRequest.getItens();



        if (itensRequest == null ||
                itensRequest.isEmpty()) {


            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Pedido precisa ter itens."
            );
        }





        Pedido pedido = new Pedido();


        pedido.setCanalPedido(
                pedidoRequest.getCanalPedido()
        );


        pedido.setClienteId(
                pedidoRequest.getClienteId()
        );


        pedido.setUnidade(unidade);




        BigDecimal valorTotal = BigDecimal.ZERO;




        for (ItemPedidoRequest itemRequest : itensRequest) {



            if (itemRequest.getProdutoId() == null ||
                    itemRequest.getQuantidade() == null ||
                    itemRequest.getQuantidade() <= 0) {


                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Produto e quantidade inválidos."
                );
            }





            Produto produto =
                    produtoRepository.findById(
                            itemRequest.getProdutoId()
                    )
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Produto não encontrado."
                            )
                    );






            Optional<Estoque> estoqueOptional =
                    estoqueRepository
                            .findByUnidadeIdAndProdutoId(
                                    unidade.getId(),
                                    produto.getId()
                            );





            Estoque estoque =
                    estoqueOptional.orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Estoque não encontrado."
                            )
                    );





            if (estoque.getQuantidade()
                    < itemRequest.getQuantidade()) {


                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Estoque insuficiente para "
                                + produto.getNome()
                );
            }






            estoque.setQuantidade(
                    estoque.getQuantidade()
                            - itemRequest.getQuantidade()
            );


            estoqueRepository.save(estoque);






            BigDecimal subtotal =
                    produto.getPreco()
                            .multiply(
                                    BigDecimal.valueOf(
                                            itemRequest.getQuantidade()
                                    )
                            );






            ItemPedido item =
                    new ItemPedido(
                            pedido,
                            produto,
                            itemRequest.getQuantidade(),
                            produto.getPreco()
                    );



            // IMPORTANTE PARA RELACIONAMENTO
            item.setPedido(pedido);



            pedido.getItens().add(item);



            valorTotal =
                    valorTotal.add(subtotal);

        }





        pedido.setValorTotal(valorTotal);




        return pedidoRepository.save(pedido);

    }






    @Transactional
    public void devolverEstoque(Pedido pedido) {



        if (pedido == null ||
                pedido.getItens() == null) {

            return;
        }





        for(ItemPedido item : pedido.getItens()) {



            Estoque estoque =
                    estoqueRepository
                            .findByUnidadeIdAndProdutoId(
                                    pedido.getUnidade().getId(),
                                    item.getProduto().getId()
                            )
                            .orElseThrow(() ->
                                    new ResponseStatusException(
                                            HttpStatus.NOT_FOUND,
                                            "Estoque não encontrado."
                                    )
                            );





            estoque.setQuantidade(
                    estoque.getQuantidade()
                            + item.getQuantidade()
            );



            estoqueRepository.save(estoque);

        }

    }

}