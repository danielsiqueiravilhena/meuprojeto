package br.com.raizesdonordeste.domain.repository;

import br.com.raizesdonordeste.domain.entity.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    Optional<Estoque> findByProdutoId(Long produtoId);

    Optional<Estoque> findByUnidadeIdAndProdutoId(Long unidadeId, Long produtoId);
}