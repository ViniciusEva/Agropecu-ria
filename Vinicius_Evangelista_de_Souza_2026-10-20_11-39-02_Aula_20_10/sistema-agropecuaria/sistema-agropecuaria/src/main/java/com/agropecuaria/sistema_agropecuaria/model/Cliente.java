package com.agropecuaria.sistema_agropecuaria.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Cliente")
@Getter
@Setter
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCliente;

    @Column(nullable = false, length = 100)
    private String nomeCliente;

    @Column(nullable = false, unique = true, length = 20)
    private String cpfCnpj;

    @Column(length = 20)
    private String telefone;

    @Column(length = 50)
    private String cidade;

    public Cliente(String nomeCliente, String cpfCnpj, String telefone, String cidade) {
        this.nomeCliente = nomeCliente;
        this.cpfCnpj = cpfCnpj;
        this.telefone = telefone;
        this.cidade = cidade;
    }
}