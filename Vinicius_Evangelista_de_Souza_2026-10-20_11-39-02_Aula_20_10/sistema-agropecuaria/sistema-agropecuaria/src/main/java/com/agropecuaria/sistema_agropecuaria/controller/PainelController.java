package com.agropecuaria.sistema_agropecuaria.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.agropecuaria.sistema_agropecuaria.model.Cliente;
import com.agropecuaria.sistema_agropecuaria.model.ProdutoAgropecuario;
import com.agropecuaria.sistema_agropecuaria.model.Venda;
import com.agropecuaria.sistema_agropecuaria.repository.ClienteRepository;
import com.agropecuaria.sistema_agropecuaria.repository.ProdutoAgropecuarioRepository;
import com.agropecuaria.sistema_agropecuaria.repository.VendaRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/painel")
@RequiredArgsConstructor
public class PainelController {

    private final ClienteRepository clienteRepository;
    private final ProdutoAgropecuarioRepository produtoRepository;
    private final VendaRepository vendaRepository;

    @GetMapping({"", "/"})
    public String inicio(Model model) {
        model.addAttribute("totalClientes", clienteRepository.count());
        model.addAttribute("totalProdutos", produtoRepository.count());
        model.addAttribute("totalVendas", vendaRepository.count());
        return "inicio";
    }

    @GetMapping("/clientes")
    public String clientes(Model model) {
        model.addAttribute("clientes", clienteRepository.findAll());
        return "clientes";
    }

    @GetMapping("/clientes/novo")
    public String novoCliente(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("edicao", false);
        return "cliente-form";
    }

    @GetMapping("/clientes/{id}/editar")
    public String editarCliente(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", obterCliente(id));
        model.addAttribute("edicao", true);
        return "cliente-form";
    }

    @PostMapping("/clientes")
    public String salvarCliente(@ModelAttribute Cliente cliente, RedirectAttributes redirectAttributes) {
        clienteRepository.save(cliente);
        redirectAttributes.addFlashAttribute("mensagem", "Cliente cadastrado com sucesso.");
        return "redirect:/painel/clientes";
    }

    @PostMapping("/clientes/{id}")
    public String atualizarCliente(@PathVariable Long id, @ModelAttribute Cliente dados,
            RedirectAttributes redirectAttributes) {
        Cliente cliente = obterCliente(id);
        cliente.setNomeCliente(dados.getNomeCliente());
        cliente.setCpfCnpj(dados.getCpfCnpj());
        cliente.setTelefone(dados.getTelefone());
        cliente.setCidade(dados.getCidade());
        clienteRepository.save(cliente);
        redirectAttributes.addFlashAttribute("mensagem", "Cliente atualizado com sucesso.");
        return "redirect:/painel/clientes";
    }

    @PostMapping("/clientes/{id}/excluir")
    public String excluirCliente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Cliente cliente = obterCliente(id);
        if (vendaRepository.existsByCliente_IdCliente(id)) {
            redirectAttributes.addFlashAttribute("erro",
                    "Este cliente possui vendas vinculadas e não pode ser excluído.");
        } else {
            clienteRepository.delete(cliente);
            redirectAttributes.addFlashAttribute("mensagem", "Cliente excluído com sucesso.");
        }
        return "redirect:/painel/clientes";
    }

    @GetMapping("/produtos")
    public String produtos(Model model) {
        model.addAttribute("produtos", produtoRepository.findAll());
        return "produtos";
    }

    @GetMapping("/produtos/novo")
    public String novoProduto(Model model) {
        model.addAttribute("produto", new ProdutoAgropecuario());
        model.addAttribute("edicao", false);
        return "produto-form";
    }

    @GetMapping("/produtos/{id}/editar")
    public String editarProduto(@PathVariable Long id, Model model) {
        model.addAttribute("produto", obterProduto(id));
        model.addAttribute("edicao", true);
        return "produto-form";
    }

    @PostMapping("/produtos")
    public String salvarProduto(@ModelAttribute ProdutoAgropecuario produto, RedirectAttributes redirectAttributes) {
        produtoRepository.save(produto);
        redirectAttributes.addFlashAttribute("mensagem", "Produto cadastrado com sucesso.");
        return "redirect:/painel/produtos";
    }

    @PostMapping("/produtos/{id}")
    public String atualizarProduto(@PathVariable Long id, @ModelAttribute ProdutoAgropecuario dados,
            RedirectAttributes redirectAttributes) {
        ProdutoAgropecuario produto = obterProduto(id);
        produto.setNomeProduto(dados.getNomeProduto());
        produto.setCategoriaProduto(dados.getCategoriaProduto());
        produto.setPrecoProduto(dados.getPrecoProduto());
        produto.setQuantidadeEstoque(dados.getQuantidadeEstoque());
        produto.setUnidadeMedida(dados.getUnidadeMedida());
        produtoRepository.save(produto);
        redirectAttributes.addFlashAttribute("mensagem", "Produto atualizado com sucesso.");
        return "redirect:/painel/produtos";
    }

    @PostMapping("/produtos/{id}/excluir")
    public String excluirProduto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        ProdutoAgropecuario produto = obterProduto(id);
        if (vendaRepository.existsByProduto_IdProduto(id)) {
            redirectAttributes.addFlashAttribute("erro",
                    "Este produto possui vendas vinculadas e não pode ser excluído.");
        } else {
            produtoRepository.delete(produto);
            redirectAttributes.addFlashAttribute("mensagem", "Produto excluído com sucesso.");
        }
        return "redirect:/painel/produtos";
    }

    @GetMapping("/vendas")
    public String vendas(Model model) {
        model.addAttribute("vendas", vendaRepository.findAll());
        return "vendas";
    }

    @GetMapping("/vendas/novo")
    public String novaVenda(Model model) {
        prepararFormularioVenda(model, new Venda(), false);
        return "venda-form";
    }

    @GetMapping("/vendas/{id}/editar")
    public String editarVenda(@PathVariable Long id, Model model) {
        prepararFormularioVenda(model, obterVenda(id), true);
        return "venda-form";
    }

    @PostMapping("/vendas")
    public String salvarVenda(@RequestParam Long clienteId, @RequestParam Long produtoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataVenda,
            @RequestParam Double valorTotal, @RequestParam(required = false) String formaPagamento,
            RedirectAttributes redirectAttributes) {
        Venda venda = new Venda();
        preencherVenda(venda, clienteId, produtoId, dataVenda, valorTotal, formaPagamento);
        vendaRepository.save(venda);
        redirectAttributes.addFlashAttribute("mensagem", "Venda cadastrada com sucesso.");
        return "redirect:/painel/vendas";
    }

    @PostMapping("/vendas/{id}")
    public String atualizarVenda(@PathVariable Long id, @RequestParam Long clienteId, @RequestParam Long produtoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataVenda,
            @RequestParam Double valorTotal, @RequestParam(required = false) String formaPagamento,
            RedirectAttributes redirectAttributes) {
        Venda venda = obterVenda(id);
        preencherVenda(venda, clienteId, produtoId, dataVenda, valorTotal, formaPagamento);
        vendaRepository.save(venda);
        redirectAttributes.addFlashAttribute("mensagem", "Venda atualizada com sucesso.");
        return "redirect:/painel/vendas";
    }

    @PostMapping("/vendas/{id}/excluir")
    public String excluirVenda(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        vendaRepository.delete(obterVenda(id));
        redirectAttributes.addFlashAttribute("mensagem", "Venda excluída com sucesso.");
        return "redirect:/painel/vendas";
    }

    private void prepararFormularioVenda(Model model, Venda venda, boolean edicao) {
        model.addAttribute("venda", venda);
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("produtos", produtoRepository.findAll());
        model.addAttribute("edicao", edicao);
    }

    private void preencherVenda(Venda venda, Long clienteId, Long produtoId, LocalDate dataVenda,
            Double valorTotal, String formaPagamento) {
        venda.setCliente(clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado")));
        venda.setProduto(produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado")));
        venda.setDataVenda(dataVenda);
        venda.setValorTotal(valorTotal);
        venda.setFormaPagamento(formaPagamento);
    }

    private Cliente obterCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    private ProdutoAgropecuario obterProduto(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
    }

    private Venda obterVenda(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venda não encontrada"));
    }
}
