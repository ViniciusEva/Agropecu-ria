package com.agropecuaria.sistema_agropecuaria.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agropecuaria.sistema_agropecuaria.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}