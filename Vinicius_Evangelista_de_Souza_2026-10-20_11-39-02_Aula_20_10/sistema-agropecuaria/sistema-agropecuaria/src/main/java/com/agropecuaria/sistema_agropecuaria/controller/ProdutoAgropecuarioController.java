package com.agropecuaria.sistema_agropecuaria.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.agropecuaria.sistema_agropecuaria.model.ProdutoAgropecuario;
import com.agropecuaria.sistema_agropecuaria.repository.ProdutoAgropecuarioRepository;
import com.agropecuaria.sistema_agropecuaria.repository.VendaRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoAgropecuarioController {

    private final ProdutoAgropecuarioRepository produtoRepository;
    private final VendaRepository vendaRepository;

    @PostMapping
    public ProdutoAgropecuario criar(@RequestBody ProdutoAgropecuario produto) {
        return produtoRepository.save(produto);
    }

    @GetMapping
    public List<ProdutoAgropecuario> listar() {
        return produtoRepository.findAll();
    }

    @PutMapping("/{id}")
    public ProdutoAgropecuario atualizar(@PathVariable Long id, @RequestBody ProdutoAgropecuario produtoAtualizado) {
        ProdutoAgropecuario produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
        produto.setNomeProduto(produtoAtualizado.getNomeProduto());
        produto.setCategoriaProduto(produtoAtualizado.getCategoriaProduto());
        produto.setPrecoProduto(produtoAtualizado.getPrecoProduto());
        produto.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());
        produto.setUnidadeMedida(produtoAtualizado.getUnidadeMedida());
        return produtoRepository.save(produto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (vendaRepository.existsByProduto_IdProduto(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Produto possui vendas vinculadas");
        }
        if (!produtoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado");
        }
        produtoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}