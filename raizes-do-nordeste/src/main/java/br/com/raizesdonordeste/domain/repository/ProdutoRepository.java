package br.com.raizesdonordeste.domain.repository;

import br.com.raizesdonordeste.domain.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

}