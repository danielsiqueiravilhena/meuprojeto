package br.com.raizesdonordeste.domain.repository;

import br.com.raizesdonordeste.domain.entity.Auditoria;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaRepository
        extends JpaRepository<Auditoria, Long> {
}