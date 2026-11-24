package com.baozistore.controller;

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

import com.baozistore.model.Cliente;
import com.baozistore.model.Pedido;
import com.baozistore.model.Produto;
import com.baozistore.repository.ClienteRepository;
import com.baozistore.repository.PedidoRepository;
import com.baozistore.repository.ProdutoRepository;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

	private final PedidoRepository pedidoRepository;
	private final ClienteRepository clienteRepository;
	private final ProdutoRepository produtoRepository;

	public PedidoController(PedidoRepository pedidoRepository, ClienteRepository clienteRepository,
			ProdutoRepository produtoRepository) {
		this.pedidoRepository = pedidoRepository;
		this.clienteRepository = clienteRepository;
		this.produtoRepository = produtoRepository;
	}

	// POST /pedidos - criar
	// Exemplo de corpo: {"cliente": {"id": 1}, "produto": {"id": 1}, "quantidade": 3}
	@PostMapping
	public ResponseEntity<Pedido> criar(@RequestBody Pedido dados) {
		Pedido pedido = new Pedido();
		preencher(pedido, dados);
		Pedido salvo = pedidoRepository.save(pedido);
		return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
	}

	// GET /pedidos/{id} - consultar por ID
	@GetMapping("/{id}")
	public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
		return pedidoRepository.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	// GET /pedidos - listar todos
	@GetMapping
	public List<Pedido> listar() {
		return pedidoRepository.findAll();
	}

	// PUT /pedidos/{id} - atualizar (opcional)
	@PutMapping("/{id}")
	public ResponseEntity<Pedido> atualizar(@PathVariable Long id, @RequestBody Pedido dados) {
		return pedidoRepository.findById(id).map(pedido -> {
			preencher(pedido, dados);
			return ResponseEntity.ok(pedidoRepository.save(pedido));
		}).orElse(ResponseEntity.notFound().build());
	}

	// DELETE /pedidos/{id} - apagar
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> apagar(@PathVariable Long id) {
		if (!pedidoRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		pedidoRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	// Valida os dados recebidos e liga o pedido ao cliente e ao produto já cadastrados
	private void preencher(Pedido pedido, Pedido dados) {
		if (dados.getCliente() == null || dados.getCliente().getId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Informe o cliente do pedido (cliente.id).");
		}
		if (dados.getProduto() == null || dados.getProduto().getId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Informe o produto do pedido (produto.id).");
		}
		if (dados.getQuantidade() == null || dados.getQuantidade() <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"A quantidade deve ser um número inteiro maior que zero.");
		}

		Cliente cliente = clienteRepository.findById(dados.getCliente().getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Cliente não encontrado."));
		Produto produto = produtoRepository.findById(dados.getProduto().getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Produto não encontrado."));

		if (!Boolean.TRUE.equals(produto.getEstoque())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto sem estoque.");
		}

		pedido.setCliente(cliente);
		pedido.setProduto(produto);
		pedido.setQuantidade(dados.getQuantidade());
	}
}
