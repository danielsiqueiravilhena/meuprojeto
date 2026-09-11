package br.com.raizesdonordeste.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "estoque",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_estoque_unidade_produto",
            columnNames = {"unidade_id", "produto_id"}
        )
    }
)
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "unidade_id", nullable = false)
    private Unidade unidade;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    public Estoque() {
    }

    public Estoque(Unidade unidade, Produto produto, Integer quantidade) {
        this.unidade = unidade;
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public Long getId() {
        return id;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}