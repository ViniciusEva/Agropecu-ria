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
@Table(name = "ProdutoAgropecuario")
@Getter
@Setter
@NoArgsConstructor
public class ProdutoAgropecuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProduto;

    @Column(nullable = false, length = 100)
    private String nomeProduto;

    @Column(length = 50)
    private String categoriaProduto;

    @Column(nullable = false)
    private Double precoProduto;

    @Column(nullable = false)
    private Integer quantidadeEstoque;

    @Column(nullable = false, length = 20)
    private String unidadeMedida = "unidade";

    public ProdutoAgropecuario(String nomeProduto, String categoriaProduto, Double precoProduto, Integer quantidadeEstoque) {
        this.nomeProduto = nomeProduto;
        this.categoriaProduto = categoriaProduto;
        this.precoProduto = precoProduto;
        this.quantidadeEstoque = quantidadeEstoque;
    }
}