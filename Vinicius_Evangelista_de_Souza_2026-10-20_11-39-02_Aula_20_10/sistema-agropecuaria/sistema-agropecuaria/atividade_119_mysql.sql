DROP DATABASE IF EXISTS sistema_agropecuaria_recuperacao;
CREATE DATABASE sistema_agropecuaria_recuperacao
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE sistema_agropecuaria_recuperacao;

CREATE TABLE ProdutoAgropecuario (
    idProduto BIGINT NOT NULL AUTO_INCREMENT,
    nomeProduto VARCHAR(100) NOT NULL,
    categoriaProduto VARCHAR(50),
    precoProduto DECIMAL(10, 2) NOT NULL,
    quantidadeEstoque INT NOT NULL,
    PRIMARY KEY (idProduto)
) ENGINE=InnoDB;

CREATE TABLE Cliente (
    idCliente BIGINT NOT NULL AUTO_INCREMENT,
    nomeCliente VARCHAR(100) NOT NULL,
    cpfCnpj VARCHAR(20) NOT NULL,
    telefone VARCHAR(20),
    cidade VARCHAR(50),
    PRIMARY KEY (idCliente),
    UNIQUE (cpfCnpj)
) ENGINE=InnoDB;

CREATE TABLE Venda (
    idVenda BIGINT NOT NULL AUTO_INCREMENT,
    idCliente BIGINT NOT NULL,
    idProduto BIGINT NOT NULL,
    dataVenda DATE NOT NULL,
    valorTotal DECIMAL(10, 2) NOT NULL,
    formaPagamento VARCHAR(30),
    PRIMARY KEY (idVenda),
    CONSTRAINT fk_venda_cliente
        FOREIGN KEY (idCliente) REFERENCES Cliente (idCliente)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_venda_produto
        FOREIGN KEY (idProduto) REFERENCES ProdutoAgropecuario (idProduto)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;


ALTER TABLE ProdutoAgropecuario
    ADD COLUMN unidadeMedida VARCHAR(20) NOT NULL DEFAULT 'unidade';

INSERT INTO ProdutoAgropecuario
    (idProduto, nomeProduto, categoriaProduto, precoProduto, quantidadeEstoque, unidadeMedida)
VALUES
    (1, 'Semente de milho', 'Sementes', 145.90, 30, 'saco'),
    (2, 'Adubo organico', 'Fertilizantes', 78.50, 45, 'kg'),
    (3, 'Sal mineral', 'Nutricao animal', 92.00, 18, 'saco');

INSERT INTO Cliente (idCliente, nomeCliente, cpfCnpj, telefone, cidade)
VALUES
    (1, 'Fazenda Boa Terra', '12345678000190', '1933331000', 'Campinas'),
    (2, 'Joao Ribeiro', '12345678901', '19988887777', 'Limeira'),
    (3, 'Sitio Santa Luzia', '98765432000110', '1932222000', 'Piracicaba');

INSERT INTO Venda (idVenda, idCliente, idProduto, dataVenda, valorTotal, formaPagamento)
VALUES
    (1, 1, 1, '2026-10-10', 437.70, 'Pix'),
    (2, 2, 2, '2026-10-12', 157.00, 'Cartao'),
    (3, 3, 3, '2026-10-15', 184.00, 'Dinheiro');


UPDATE ProdutoAgropecuario
SET precoProduto = 149.90, quantidadeEstoque = 28
WHERE idProduto = 1;

UPDATE Cliente
SET telefone = '1933331010', cidade = 'Valinhos'
WHERE idCliente = 1;

UPDATE Venda
SET formaPagamento = 'Transferencia', valorTotal = 449.70
WHERE idVenda = 1;


DELETE FROM Venda WHERE idVenda = 3;
DELETE FROM ProdutoAgropecuario WHERE idProduto = 3;
DELETE FROM Cliente WHERE idCliente = 3;


SELECT idProduto, nomeProduto, categoriaProduto, precoProduto, unidadeMedida
FROM ProdutoAgropecuario
ORDER BY nomeProduto;

SELECT idProduto, nomeProduto, quantidadeEstoque, unidadeMedida
FROM ProdutoAgropecuario
WHERE quantidadeEstoque < 30
ORDER BY quantidadeEstoque ASC;

SELECT idCliente, nomeCliente, cpfCnpj, telefone, cidade
FROM Cliente
ORDER BY nomeCliente;

SELECT idCliente, nomeCliente, cidade
FROM Cliente
WHERE cidade = 'Valinhos'
ORDER BY nomeCliente;

SELECT v.idVenda, v.dataVenda, c.nomeCliente, p.nomeProduto,
       v.valorTotal, v.formaPagamento
FROM Venda AS v
INNER JOIN Cliente AS c ON c.idCliente = v.idCliente
INNER JOIN ProdutoAgropecuario AS p ON p.idProduto = v.idProduto
ORDER BY v.dataVenda DESC;

SELECT v.idVenda, v.dataVenda, c.nomeCliente, v.valorTotal
FROM Venda AS v
INNER JOIN Cliente AS c ON c.idCliente = v.idCliente
WHERE v.valorTotal >= 200.00
ORDER BY v.valorTotal DESC;
