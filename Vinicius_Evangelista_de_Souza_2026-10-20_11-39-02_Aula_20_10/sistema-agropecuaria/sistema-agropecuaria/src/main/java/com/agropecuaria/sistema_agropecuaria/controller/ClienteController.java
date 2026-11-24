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

import com.agropecuaria.sistema_agropecuaria.model.Cliente;
import com.agropecuaria.sistema_agropecuaria.repository.ClienteRepository;
import com.agropecuaria.sistema_agropecuaria.repository.VendaRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteRepository clienteRepository;
    private final VendaRepository vendaRepository;

    @PostMapping
    public Cliente criar(@RequestBody Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @GetMapping
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    @PutMapping("/{id}")
    public Cliente atualizar(@PathVariable Long id, @RequestBody Cliente clienteAtualizado) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
        cliente.setNomeCliente(clienteAtualizado.getNomeCliente());
        cliente.setCpfCnpj(clienteAtualizado.getCpfCnpj());
        cliente.setTelefone(clienteAtualizado.getTelefone());
        cliente.setCidade(clienteAtualizado.getCidade());
        return clienteRepository.save(cliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (vendaRepository.existsByCliente_IdCliente(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cliente possui vendas vinculadas");
        }
        if (!clienteRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado");
        }
        clienteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}