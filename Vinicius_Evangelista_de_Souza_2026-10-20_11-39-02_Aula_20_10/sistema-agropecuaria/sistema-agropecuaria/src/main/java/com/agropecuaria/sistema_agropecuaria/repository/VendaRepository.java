package com.agropecuaria.sistema_agropecuaria.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agropecuaria.sistema_agropecuaria.model.Venda;

public interface VendaRepository extends JpaRepository<Venda, Long> {
    boolean existsByCliente_IdCliente(Long clienteId);

    boolean existsByProduto_IdProduto(Long produtoId);
}