package br.com.raizesdonordeste.domain.entity;

import br.com.raizesdonordeste.domain.enums.FormaPagamento;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "pagamentos")
public class Pagamento {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "pedido_id",
            nullable = false,
            unique = true
    )
    @JsonIgnore
    private Pedido pedido;



    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FormaPagamento formaPagamento;



    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;



    @Column(nullable = false, length = 20)
    private String status;



    @Column(nullable = false)
    private LocalDateTime dataPagamento;



    public Pagamento() {

    }



    public Pagamento(
            Pedido pedido,
            FormaPagamento formaPagamento,
            BigDecimal valor,
            String status) {

        this.pedido = pedido;
        this.formaPagamento = formaPagamento;
        this.valor = valor;
        this.status = status;
        this.dataPagamento = LocalDateTime.now();

    }



    public Long getId() {
        return id;
    }



    public Pedido getPedido() {
        return pedido;
    }



    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }



    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }



    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }



    public BigDecimal getValor() {
        return valor;
    }



    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }



    public String getStatus() {
        return status;
    }



    public void setStatus(String status) {
        this.status = status;
    }



    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }



    public void setDataPagamento(LocalDateTime dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

}