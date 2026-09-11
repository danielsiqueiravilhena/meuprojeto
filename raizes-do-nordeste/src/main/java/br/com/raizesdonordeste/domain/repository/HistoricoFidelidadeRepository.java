package br.com.raizesdonordeste.domain.repository;

import br.com.raizesdonordeste.domain.entity.HistoricoFidelidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoFidelidadeRepository
        extends JpaRepository<HistoricoFidelidade, Long> {

    List<HistoricoFidelidade> findByClienteIdOrderByDataHoraDesc(
            Long clienteId
    );
}