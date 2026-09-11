package br.com.raizesdonordeste.domain.repository;

import br.com.raizesdonordeste.domain.entity.Pedido;
import br.com.raizesdonordeste.domain.enums.CanalPedido;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PedidoRepository 
        extends JpaRepository<Pedido, Long> {


    List<Pedido> findByCanalPedido(CanalPedido canalPedido);


}