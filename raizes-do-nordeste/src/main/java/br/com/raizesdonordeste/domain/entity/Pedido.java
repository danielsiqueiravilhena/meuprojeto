package br.com.raizesdonordeste.domain.entity;

import br.com.raizesdonordeste.domain.enums.CanalPedido;
import br.com.raizesdonordeste.domain.enums.StatusPedido;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "pedidos")
public class Pedido {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusPedido status = StatusPedido.AGUARDANDO_PAGAMENTO;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CanalPedido canalPedido;


    @Column(nullable = false)
    private Long clienteId;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "unidade_id", nullable = false)
    private Unidade unidade;


    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;


    @Column(nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();



    @OneToMany(
            mappedBy = "pedido",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JsonManagedReference
    private List<ItemPedido> itens = new ArrayList<>();



    public Pedido() {

    }



    public Pedido(CanalPedido canalPedido, BigDecimal valorTotal) {

        this.canalPedido = canalPedido;
        this.valorTotal = valorTotal;
        this.status = StatusPedido.AGUARDANDO_PAGAMENTO;
        this.dataCriacao = LocalDateTime.now();

    }



    public Long getId() {
        return id;
    }



    public StatusPedido getStatus() {
        return status;
    }



    public void setStatus(StatusPedido status) {
        this.status = status;
    }



    public CanalPedido getCanalPedido() {
        return canalPedido;
    }



    public void setCanalPedido(CanalPedido canalPedido) {
        this.canalPedido = canalPedido;
    }



    public Long getClienteId() {
        return clienteId;
    }



    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }



    public Unidade getUnidade() {
        return unidade;
    }



    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
    }



    public BigDecimal getValorTotal() {
        return valorTotal;
    }



    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }



    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }



    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }



    public List<ItemPedido> getItens() {
        return itens;
    }



    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }


}