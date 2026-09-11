package br.com.raizesdonordeste.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String usuario;

    @Column(nullable = false, length = 20)
    private String metodo;

    @Column(nullable = false, length = 255)
    private String rota;

    @Column(nullable = false)
    private Integer statusHttp;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    public Auditoria() {
    }

    public Auditoria(
            String usuario,
            String metodo,
            String rota,
            Integer statusHttp) {

        this.usuario = usuario;
        this.metodo = metodo;
        this.rota = rota;
        this.statusHttp = statusHttp;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public String getRota() {
        return rota;
    }

    public void setRota(String rota) {
        this.rota = rota;
    }

    public Integer getStatusHttp() {
        return statusHttp;
    }

    public void setStatusHttp(Integer statusHttp) {
        this.statusHttp = statusHttp;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}