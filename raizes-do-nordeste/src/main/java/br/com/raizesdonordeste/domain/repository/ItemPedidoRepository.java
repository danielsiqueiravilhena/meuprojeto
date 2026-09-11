package br.com.raizesdonordeste.domain.repository;

import br.com.raizesdonordeste.domain.entity.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
}