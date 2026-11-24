# Sistema de Controle de Loja Agropecuaria

Aplicacao individual para uma loja agropecuaria, com cadastro e consulta de clientes, produtos e vendas. O projeto integra uma API REST em Spring Boot com uma interface HTML renderizada pelo Thymeleaf.

## Estudo de caso

O sistema organiza tres entidades relacionadas:

- **Cliente**: nome, CPF/CNPJ, telefone e cidade.
- **Produto agropecuario**: nome, categoria, preco, quantidade em estoque e unidade de medida.
- **Venda**: cliente, produto, data, valor total e forma de pagamento.

Cada venda referencia um cliente e um produto. A aplicacao impede a exclusao de clientes e produtos que possuam vendas relacionadas.

## Tecnologias

- Java 25 e Spring Boot 4
- Spring Web MVC e Spring Data JPA
- Thymeleaf, HTML e CSS
- H2 em memoria para a aplicacao
- Lombok para gerar getters, setters e construtores das entidades

## Executar

Requisitos: JDK 25 instalado.

No PowerShell, na pasta deste projeto (a pasta que contem `pom.xml`), execute:

```powershell
.\mvnw.cmd spring-boot:run
```

Depois, abra [http://localhost:8080/](http://localhost:8080/). A raiz redireciona para o painel em `/painel`.

O banco H2 da aplicacao e temporario: os dados ficam em memoria e sao apagados quando a aplicacao e encerrada.

### Console H2

O console do banco fica em [http://localhost:8080/h2-console](http://localhost:8080/h2-console).

- JDBC URL: `jdbc:h2:mem:sistema_agropecuaria`
- Usuario: `sa`
- Senha: em branco

## Interface web

| Tela | Rota |
| --- | --- |
| Visao geral | `/painel` |
| Clientes | `/painel/clientes` |
| Produtos | `/painel/produtos` |
| Vendas | `/painel/vendas` |

As telas permitem cadastrar, listar, editar e excluir registros. Produtos e clientes referenciados por vendas nao podem ser excluidos.

## API REST

As rotas aceitam e retornam JSON. Para criar ou atualizar registros, envie `Content-Type: application/json`.

| Recurso | Listar | Criar | Atualizar | Excluir |
| --- | --- | --- | --- | --- |
| Clientes | `GET /clientes` | `POST /clientes` | `PUT /clientes/{id}` | `DELETE /clientes/{id}` |
| Produtos | `GET /produtos` | `POST /produtos` | `PUT /produtos/{id}` | `DELETE /produtos/{id}` |
| Vendas | `GET /vendas` | `POST /vendas` | `PUT /vendas/{id}` | `DELETE /vendas/{id}` |

Exemplo de criacao de cliente:

```json
{
  "nomeCliente": "Joao da Silva",
  "cpfCnpj": "12345678901",
  "telefone": "11999990000",
  "cidade": "Campinas"
}
```

Exemplo de criacao de produto:

```json
{
  "nomeProduto": "Semente de milho",
  "categoriaProduto": "Sementes",
  "precoProduto": 145.90,
  "quantidadeEstoque": 30,
  "unidadeMedida": "saco"
}
```

Exemplo de criacao de venda (o cliente e o produto devem existir):

```json
{
  "cliente": {
    "idCliente": 1
  },
  "produto": {
    "idProduto": 1
  },
  "dataVenda": "2026-11-24",
  "valorTotal": 145.90,
  "formaPagamento": "Pix"
}
```

Ao excluir um registro inexistente, a API responde `404`. Ao tentar excluir um cliente ou produto relacionado a uma venda, responde `409`.

## Testes

Para executar os testes automatizados:

```powershell
.\mvnw.cmd test
```

Os testes usam MockMvc para verificar as rotas web e a API, incluindo operacoes CRUD e integridade referencial. Eles nao substituem a evidencia de testes feitos no Postman.

## Script SQL da atividade

O arquivo [`atividade_119_mysql.sql`](./atividade_119_mysql.sql) contem uma versao da atividade para MySQL: criacao das tres tabelas, chave estrangeira, `ALTER TABLE` para adicionar `unidadeMedida`, comandos de insercao, atualizacao, exclusao e consultas.

Esse script e independente do banco H2 usado pela aplicacao. **Ele comeca removendo o banco `sistema_agropecuaria_recuperacao` para recria-lo**; execute-o somente em um ambiente de atividade onde esse banco possa ser apagado.

## Evidencias para a entrega

Os testes automatizados do projeto sao executados com MockMvc. Para completar a entrega da disciplina, adicione tambem a Collection exportada ou capturas das requisicoes no Postman e capturas de tela da interface em funcionamento.
