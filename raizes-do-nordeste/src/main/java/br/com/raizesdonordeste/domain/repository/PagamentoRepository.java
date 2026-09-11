package br.com.raizesdonordeste.domain.repository;

import br.com.raizesdonordeste.domain.entity.Pagamento;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PagamentoRepository 
        extends JpaRepository<Pagamento, Long> {


    Optional<Pagamento> findByPedidoId(Long pedidoId);


}