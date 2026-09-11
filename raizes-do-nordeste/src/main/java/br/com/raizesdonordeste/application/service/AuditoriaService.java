package br.com.raizesdonordeste.application.service;

import br.com.raizesdonordeste.domain.entity.Auditoria;
import br.com.raizesdonordeste.domain.repository.AuditoriaRepository;

import org.springframework.stereotype.Service;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(
            AuditoriaRepository auditoriaRepository) {

        this.auditoriaRepository = auditoriaRepository;
    }

    public Auditoria registrar(
            String usuario,
            String metodo,
            String rota,
            Integer statusHttp) {

        Auditoria auditoria = new Auditoria(
                usuario,
                metodo,
                rota,
                statusHttp
        );

        return auditoriaRepository.save(auditoria);
    }
}