package com.baozistore.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
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

import com.baozistore.model.Produto;
import com.baozistore.repository.ProdutoRepository;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

	private final ProdutoRepository repository;

	public ProdutoController(ProdutoRepository repository) {
		this.repository = repository;
	}

	// POST /produtos - criar
	@PostMapping
	public ResponseEntity<Produto> criar(@RequestBody Produto dados) {
		validar(dados);
		Produto produto = new Produto();
		preencher(produto, dados);
		Produto salvo = repository.save(produto);
		return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
	}

	// GET /produtos/{id} - consultar por ID
	@GetMapping("/{id}")
	public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
		return repository.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	// GET /produtos - listar todos
	@GetMapping
	public List<Produto> listar() {
		return repository.findAll();
	}

	// PUT /produtos/{id} - atualizar (opcional)
	@PutMapping("/{id}")
	public ResponseEntity<Produto> atualizar(@PathVariable Long id, @RequestBody Produto dados) {
		validar(dados);
		return repository.findById(id).map(produto -> {
			preencher(produto, dados);
			return ResponseEntity.ok(repository.save(produto));
		}).orElse(ResponseEntity.notFound().build());
	}

	// DELETE /produtos/{id} - apagar
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> apagar(@PathVariable Long id) {
		if (!repository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		try {
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Não é possível apagar o produto porque existem pedidos vinculados a ele.");
		}
		return ResponseEntity.noContent().build();
	}

	private void preencher(Produto produto, Produto dados) {
		produto.setNome(dados.getNome().trim());
		produto.setPreco(dados.getPreco());
		// se o estoque não for informado, considera o produto disponível
		produto.setEstoque(dados.getEstoque() != null ? dados.getEstoque() : Boolean.TRUE);
	}

	private void validar(Produto dados) {
		if (dados.getNome() == null || dados.getNome().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O campo nome é obrigatório.");
		}
		if (dados.getPreco() == null || dados.getPreco().compareTo(BigDecimal.ZERO) < 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"O campo preco é obrigatório e não pode ser negativo.");
		}
	}
}
