package br.com.raizesdonordeste.domain.repository;

import br.com.raizesdonordeste.domain.entity.Fidelidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FidelidadeRepository extends JpaRepository<Fidelidade, Long> {

    Optional<Fidelidade> findByClienteId(Long clienteId);
}