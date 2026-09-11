package br.com.raizesdonordeste.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "fidelidades")
public class Fidelidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long clienteId;

    @Column(nullable = false)
    private Integer pontos = 0;

    @Column(nullable = false)
    private Boolean consentimentoLgpd = false;

    public Fidelidade() {
    }

    public Fidelidade(
            Long clienteId,
            Integer pontos,
            Boolean consentimentoLgpd) {

        this.clienteId = clienteId;
        this.pontos = pontos;
        this.consentimentoLgpd = consentimentoLgpd;
    }

    public Long getId() {
        return id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Integer getPontos() {
        return pontos;
    }

    public void setPontos(Integer pontos) {
        this.pontos = pontos;
    }

    public Boolean getConsentimentoLgpd() {
        return consentimentoLgpd;
    }

    public void setConsentimentoLgpd(Boolean consentimentoLgpd) {
        this.consentimentoLgpd = consentimentoLgpd;
    }
}