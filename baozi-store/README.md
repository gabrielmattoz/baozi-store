# Baozi Store – API REST

API REST simples para a **Baozi Store**, uma loja de pão chinês, que controla **clientes**, **produtos** e **pedidos**.
Trabalho da disciplina de Desenvolvimento Web Back-End (UNINTER).

**Aluno:** Gabriel Matos Pinho – RU 5212131

## Tecnologias

- Java 17
- Spring Boot 3.5.0 (Spring Web / MVC)
- Spring Data JPA (Hibernate)
- Banco relacional H2 (modo arquivo)
- Maven
- JSON nos endpoints
- Testes com Postman

## Estrutura (padrão MVC do Spring)

```
src/main/java/com/baozistore
├── BaoziStoreApplication.java
├── model         -> entidades JPA (Cliente, Produto, Pedido)
├── repository    -> interfaces JpaRepository
└── controller    -> controladores REST
```

## Como executar

1. Tenha o **JDK 17** (ou superior) instalado.
2. Importe o projeto na IDE como **Existing Maven Project** (ou use `mvn spring-boot:run` na pasta do projeto).
3. Execute a classe `BaoziStoreApplication`.
4. A API ficará disponível em `http://localhost:8080`.

O banco H2 é criado automaticamente na pasta `data/` (`jdbc:h2:file:./data/exemplo`, usuário `sa`, senha `senha123`).
Console do H2: `http://localhost:8080/h2-console`.

## Entidades

| Entidade | Campos |
|----------|--------|
| Cliente  | `id` (Long), `nome` (String), `clienteDesde` (LocalDate) |
| Produto  | `id` (Long), `nome` (String), `preco` (BigDecimal), `estoque` (Boolean) |
| Pedido   | `id` (Long), `cliente` (Cliente, coluna `cliente_id`), `produto` (Produto, coluna `produto_id`), `quantidade` (Integer) |

## Endpoints

Para cada recurso (`/clientes`, `/produtos`, `/pedidos`):

| Método | URL | Descrição | Retorno |
|--------|-----|-----------|---------|
| POST | `/{recurso}` | Cria | 201 Created |
| GET | `/{recurso}/{id}` | Consulta por ID | 200 OK / 404 |
| GET | `/{recurso}` | Lista todos | 200 OK |
| DELETE | `/{recurso}/{id}` | Apaga | 204 No Content / 404 |
| PUT (opcional) | `/{recurso}/{id}` | Atualiza | 200 OK / 404 |

Dados inválidos retornam `400 Bad Request`. Apagar cliente ou produto que já tem pedidos retorna `409 Conflict`.

## Exemplos de corpo (JSON)

Cliente:

```json
{ "nome": "Gabriel5212131", "clienteDesde": "2026-11-24" }
```

Produto:

```json
{ "nome": "Baozi de Carne", "preco": 8.50, "estoque": true }
```

Pedido (usa os ids do cliente e do produto já cadastrados):

```json
{ "cliente": { "id": 1 }, "produto": { "id": 1 }, "quantidade": 3 }
```

## Postman

A pasta `postman/` contém a coleção `Baozi_Store.postman_collection.json`, com todas as requisições prontas
(importe no Postman e execute na ordem, com o banco vazio).
