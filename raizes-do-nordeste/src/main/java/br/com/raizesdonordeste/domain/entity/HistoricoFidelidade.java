package br.com.raizesdonordeste.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_fidelidade")
public class HistoricoFidelidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long clienteId;

    @Column(nullable = false)
    private Integer pontos;

    @Column(nullable = false, length = 255)
    private String motivo;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    public HistoricoFidelidade() {
    }

    public HistoricoFidelidade(
            Long clienteId,
            Integer pontos,
            String motivo) {

        this.clienteId = clienteId;
        this.pontos = pontos;
        this.motivo = motivo;
        this.dataHora = LocalDateTime.now();
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}