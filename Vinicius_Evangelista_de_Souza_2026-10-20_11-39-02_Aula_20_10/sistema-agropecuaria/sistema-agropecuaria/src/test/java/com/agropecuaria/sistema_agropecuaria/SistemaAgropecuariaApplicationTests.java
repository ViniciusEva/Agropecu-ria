package com.agropecuaria.sistema_agropecuaria;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.agropecuaria.sistema_agropecuaria.repository.ClienteRepository;
import com.agropecuaria.sistema_agropecuaria.repository.ProdutoAgropecuarioRepository;
import com.agropecuaria.sistema_agropecuaria.repository.VendaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class SistemaAgropecuariaApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private VendaRepository vendaRepository;

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private ProdutoAgropecuarioRepository produtoRepository;

	@BeforeEach
	void limparDados() {
		vendaRepository.deleteAll();
		clienteRepository.deleteAll();
		produtoRepository.deleteAll();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void rendersDashboardAndEveryPanelRoute() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/painel"));
		mockMvc.perform(get("/painel")).andExpect(status().isOk()).andExpect(view().name("inicio"));
		mockMvc.perform(get("/painel/clientes")).andExpect(status().isOk()).andExpect(view().name("clientes"));
		mockMvc.perform(get("/painel/clientes/novo")).andExpect(status().isOk()).andExpect(view().name("cliente-form"));
		mockMvc.perform(get("/painel/produtos")).andExpect(status().isOk()).andExpect(view().name("produtos"));
		mockMvc.perform(get("/painel/produtos/novo")).andExpect(status().isOk()).andExpect(view().name("produto-form"));
		mockMvc.perform(get("/painel/vendas")).andExpect(status().isOk()).andExpect(view().name("vendas"));
		mockMvc.perform(get("/painel/vendas/novo")).andExpect(status().isOk()).andExpect(view().name("venda-form"));
		mockMvc.perform(get("/usuarios.css")).andExpect(status().isOk()).andExpect(content().contentType("text/css"));
	}

	@Test
	void supportsCrudThroughAgroPanel() throws Exception {
		mockMvc.perform(post("/painel/clientes")
				.param("nomeCliente", "Joao da Silva")
				.param("cpfCnpj", "12345678901")
				.param("telefone", "11999990000")
				.param("cidade", "Campinas"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/painel/clientes"));

		var cliente = clienteRepository.findAll().get(0);
		mockMvc.perform(get("/painel/clientes/" + cliente.getIdCliente() + "/editar"))
				.andExpect(status().isOk())
				.andExpect(view().name("cliente-form"));
		mockMvc.perform(post("/painel/clientes/" + cliente.getIdCliente())
				.param("nomeCliente", "Joao Atualizado")
				.param("cpfCnpj", "12345678901")
				.param("telefone", "11999990001")
				.param("cidade", "Ribeirao"))
				.andExpect(status().is3xxRedirection());

		mockMvc.perform(post("/painel/produtos")
				.param("nomeProduto", "Semente de milho")
				.param("categoriaProduto", "Sementes")
				.param("precoProduto", "25.50")
				.param("quantidadeEstoque", "12")
				.param("unidadeMedida", "saco"))
				.andExpect(status().is3xxRedirection());

		var produto = produtoRepository.findAll().get(0);
		mockMvc.perform(get("/painel/produtos/" + produto.getIdProduto() + "/editar"))
				.andExpect(status().isOk())
				.andExpect(view().name("produto-form"));
		mockMvc.perform(post("/painel/produtos/" + produto.getIdProduto())
				.param("nomeProduto", "Semente de milho selecionada")
				.param("categoriaProduto", "Sementes")
				.param("precoProduto", "27.00")
				.param("quantidadeEstoque", "15")
				.param("unidadeMedida", "saco"))
				.andExpect(status().is3xxRedirection());

		mockMvc.perform(post("/painel/vendas")
				.param("clienteId", cliente.getIdCliente().toString())
				.param("produtoId", produto.getIdProduto().toString())
				.param("dataVenda", "2026-11-24")
				.param("valorTotal", "54.00")
				.param("formaPagamento", "Pix"))
				.andExpect(status().is3xxRedirection());

		var venda = vendaRepository.findAll().get(0);
		mockMvc.perform(get("/painel/vendas/" + venda.getIdVenda() + "/editar"))
				.andExpect(status().isOk())
				.andExpect(view().name("venda-form"));
		mockMvc.perform(get("/painel/vendas"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Joao Atualizado")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Semente de milho selecionada")));
		org.junit.jupiter.api.Assertions.assertEquals("saco",
				produtoRepository.findById(produto.getIdProduto()).orElseThrow().getUnidadeMedida());

		mockMvc.perform(post("/painel/clientes/" + cliente.getIdCliente() + "/excluir"))
				.andExpect(status().is3xxRedirection());
		org.junit.jupiter.api.Assertions.assertTrue(clienteRepository.existsById(cliente.getIdCliente()));

		mockMvc.perform(post("/painel/vendas/" + venda.getIdVenda() + "/excluir"))
				.andExpect(status().is3xxRedirection());
		mockMvc.perform(post("/painel/clientes/" + cliente.getIdCliente() + "/excluir"))
				.andExpect(status().is3xxRedirection());
		mockMvc.perform(post("/painel/produtos/" + produto.getIdProduto() + "/excluir"))
				.andExpect(status().is3xxRedirection());
		org.junit.jupiter.api.Assertions.assertFalse(clienteRepository.existsById(cliente.getIdCliente()));
		org.junit.jupiter.api.Assertions.assertFalse(produtoRepository.existsById(produto.getIdProduto()));
	}

	@Test
	void supportsRestCrudRoutesAndReturnsNotFoundForMissingRecords() throws Exception {
		MvcResult clienteResponse = mockMvc.perform(post("/clientes")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nomeCliente\":\"Maria Rural\",\"cpfCnpj\":\"98765432100\",\"telefone\":\"11988887777\",\"cidade\":\"Limeira\"}"))
				.andExpect(status().isOk()).andReturn();
		JsonNode cliente = objectMapper.readTree(clienteResponse.getResponse().getContentAsString());
		long clienteId = cliente.get("idCliente").asLong();

		mockMvc.perform(get("/clientes")).andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maria Rural")));
		mockMvc.perform(put("/clientes/" + clienteId)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nomeCliente\":\"Maria Atualizada\",\"cpfCnpj\":\"98765432100\",\"telefone\":\"11988887777\",\"cidade\":\"Limeira\"}"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maria Atualizada")));

		MvcResult produtoResponse = mockMvc.perform(post("/produtos")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nomeProduto\":\"Racao\",\"categoriaProduto\":\"Insumos\",\"precoProduto\":80.0,\"quantidadeEstoque\":5,\"unidadeMedida\":\"saco\"}"))
				.andExpect(status().isOk()).andReturn();
		JsonNode produto = objectMapper.readTree(produtoResponse.getResponse().getContentAsString());
		long produtoId = produto.get("idProduto").asLong();
		mockMvc.perform(get("/produtos")).andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Racao")));
		mockMvc.perform(put("/produtos/" + produtoId)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nomeProduto\":\"Racao premium\",\"categoriaProduto\":\"Insumos\",\"precoProduto\":90.0,\"quantidadeEstoque\":8,\"unidadeMedida\":\"saco\"}"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Racao premium")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("saco")));

		MvcResult vendaResponse = mockMvc.perform(post("/vendas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"cliente\":{\"idCliente\":" + clienteId + "},\"produto\":{\"idProduto\":" + produtoId
						+ "},\"dataVenda\":\"2026-11-24\",\"valorTotal\":90.0,\"formaPagamento\":\"Dinheiro\"}"))
				.andExpect(status().isOk()).andReturn();
		JsonNode venda = objectMapper.readTree(vendaResponse.getResponse().getContentAsString());
		long vendaId = venda.get("idVenda").asLong();
		mockMvc.perform(get("/vendas")).andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Dinheiro")));
		mockMvc.perform(put("/vendas/" + vendaId)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"cliente\":{\"idCliente\":" + clienteId + "},\"produto\":{\"idProduto\":" + produtoId
						+ "},\"dataVenda\":\"2026-11-24\",\"valorTotal\":100.0,\"formaPagamento\":\"Cartao\"}"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Cartao")));

		mockMvc.perform(delete("/clientes/" + clienteId)).andExpect(status().isConflict());
		mockMvc.perform(delete("/produtos/" + produtoId)).andExpect(status().isConflict());
		mockMvc.perform(delete("/vendas/" + vendaId)).andExpect(status().isNoContent());
		mockMvc.perform(delete("/produtos/" + produtoId)).andExpect(status().isNoContent());
		mockMvc.perform(delete("/clientes/" + clienteId)).andExpect(status().isNoContent());
		mockMvc.perform(put("/clientes/999999")
				.contentType(MediaType.APPLICATION_JSON).content("{\"nomeCliente\":\"Inexistente\"}"))
				.andExpect(status().isNotFound());
		mockMvc.perform(delete("/produtos/999999")).andExpect(status().isNotFound());
		mockMvc.perform(put("/vendas/999999")
				.contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isNotFound());
	}
}
