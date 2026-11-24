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

import com.agropecuaria.sistema_agropecuaria.model.Venda;
import com.agropecuaria.sistema_agropecuaria.repository.VendaRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/vendas")
@RequiredArgsConstructor
public class VendaController {

    private final VendaRepository vendaRepository;

    @PostMapping
    public Venda criar(@RequestBody Venda venda) {
        return vendaRepository.save(venda);
    }

    @GetMapping
    public List<Venda> listar() {
        return vendaRepository.findAll();
    }

    @PutMapping("/{id}")
    public Venda atualizar(@PathVariable Long id, @RequestBody Venda vendaAtualizada) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venda não encontrada"));
        venda.setCliente(vendaAtualizada.getCliente());
        venda.setProduto(vendaAtualizada.getProduto());
        venda.setDataVenda(vendaAtualizada.getDataVenda());
        venda.setValorTotal(vendaAtualizada.getValorTotal());
        venda.setFormaPagamento(vendaAtualizada.getFormaPagamento());
        return vendaRepository.save(venda);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!vendaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Venda não encontrada");
        }
        vendaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}