package com.CRUD.estudo.resource;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.CRUD.estudo.event.RecursoCriadoEvent;
import com.CRUD.estudo.model.Pessoa;
import com.CRUD.estudo.repository.PessoaRepository;
import com.CRUD.estudo.service.PessoaService;
@CrossOrigin
@RestController
@RequestMapping(path="/pessoas")
public class PessoaResource {

	@Autowired
	private PessoaRepository pessoaRepository;

	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private PessoaService pessoaService;
	
	
	@GetMapping
	public Page<Pessoa> listar(Pageable pageable){
		
		return pessoaRepository.findAll(pageable);
		
	}
	
	@GetMapping("/{codigo}")
	public ResponseEntity<Optional<Pessoa>> buscarPeloCodigo(@PathVariable Long codigo){
	
		Optional<Pessoa> pessoa = pessoaRepository.findById(codigo);
			
		if (pessoa != null)
			return ResponseEntity.ok(pessoa);
		else
			return ResponseEntity.notFound().build();
	}
	
	@PostMapping
	public ResponseEntity<Pessoa> Criar(@Valid @RequestBody Pessoa pessoa, HttpServletResponse response) {
		Pessoa pessoaSalva = pessoaRepository.save(pessoa);
	
		publisher.publishEvent(new RecursoCriadoEvent(this, response, pessoaSalva.getCodigo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);
	}
	
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long codigo){
		
		pessoaRepository.deleteById(codigo);
	}
	
	@PutMapping("/{codigo}")
	public ResponseEntity<Pessoa> atualizar(@PathVariable Long codigo, @Valid @RequestBody Pessoa pessoa) {
		
		buscarPeloCodigo(codigo);
		
		Pessoa pessoaSalva = pessoaService.atualizar(codigo, pessoa);
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(pessoaSalva);
		
	}
		
}
