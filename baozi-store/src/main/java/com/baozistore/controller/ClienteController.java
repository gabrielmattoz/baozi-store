package com.baozistore.controller;

import java.time.LocalDate;
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

import com.baozistore.model.Cliente;
import com.baozistore.repository.ClienteRepository;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

	private final ClienteRepository repository;

	public ClienteController(ClienteRepository repository) {
		this.repository = repository;
	}

	// POST /clientes - criar
	@PostMapping
	public ResponseEntity<Cliente> criar(@RequestBody Cliente dados) {
		validar(dados);
		Cliente cliente = new Cliente();
		cliente.setNome(dados.getNome().trim());
		cliente.setClienteDesde(dados.getClienteDesde() != null ? dados.getClienteDesde() : LocalDate.now());
		Cliente salvo = repository.save(cliente);
		return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
	}

	// GET /clientes/{id} - consultar por ID
	@GetMapping("/{id}")
	public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
		return repository.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	// GET /clientes - listar todos
	@GetMapping
	public List<Cliente> listar() {
		return repository.findAll();
	}

	// PUT /clientes/{id} - atualizar (opcional)
	@PutMapping("/{id}")
	public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @RequestBody Cliente dados) {
		validar(dados);
		return repository.findById(id).map(cliente -> {
			cliente.setNome(dados.getNome().trim());
			if (dados.getClienteDesde() != null) {
				cliente.setClienteDesde(dados.getClienteDesde());
			}
			return ResponseEntity.ok(repository.save(cliente));
		}).orElse(ResponseEntity.notFound().build());
	}

	// DELETE /clientes/{id} - apagar
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> apagar(@PathVariable Long id) {
		if (!repository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		try {
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Não é possível apagar o cliente porque existem pedidos vinculados a ele.");
		}
		return ResponseEntity.noContent().build();
	}

	private void validar(Cliente dados) {
		if (dados.getNome() == null || dados.getNome().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O campo nome é obrigatório.");
		}
	}
}
