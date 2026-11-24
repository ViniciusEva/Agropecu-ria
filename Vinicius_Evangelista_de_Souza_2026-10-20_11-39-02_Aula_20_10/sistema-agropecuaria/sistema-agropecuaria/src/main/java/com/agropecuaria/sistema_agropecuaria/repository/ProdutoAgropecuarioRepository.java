package com.agropecuaria.sistema_agropecuaria.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agropecuaria.sistema_agropecuaria.model.ProdutoAgropecuario;

public interface ProdutoAgropecuarioRepository extends JpaRepository<ProdutoAgropecuario, Long> {
}