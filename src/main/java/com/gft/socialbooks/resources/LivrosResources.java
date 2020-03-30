package com.gft.socialbooks.resources;

import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gft.socialbooks.domain.Comentario;
import com.gft.socialbooks.domain.Livro;
import com.gft.socialbooks.services.LivrosService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags = "Livros")
@RestController
@RequestMapping(value = "/livros")
public class LivrosResources {
	
	@Autowired
	private LivrosService livrosService;

	@ApiOperation("Listar livros")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Livro>> listar() {

		return ResponseEntity.status(HttpStatus.OK).body(livrosService.listar());
	}
	
	@ApiOperation("Adicionar um novo livro")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Void> salvar(@ApiParam(name = "corpo", value = "Representação de um novo livro") 
			@Valid @RequestBody Livro livro) {
		livro = livrosService.salvar(livro);
		
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(livro.getId()).toUri();
		
		return ResponseEntity.created(uri).build();
	}
	
	@ApiOperation("Pesquisa por id")
	@RequestMapping(value = "/{id}",method = RequestMethod.GET)
	public ResponseEntity<?> buscar(@ApiParam(value = "ID de um livro", example = "1") @PathVariable("id") Long id) {
		Livro livro = livrosService.buscar(id);
		
		CacheControl cacheControl = CacheControl.maxAge(20, TimeUnit.SECONDS);
		
		return ResponseEntity.status(HttpStatus.OK).cacheControl(cacheControl).body(livro);
	}
	
	@ApiOperation("Excluir um livro")
	@RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
	public ResponseEntity<Void> deletar(
			@ApiParam(value = "ID de um livro", example = "1")
			@PathVariable("id") Long id) {
		livrosService.deletar(id);	
		return ResponseEntity.noContent().build();
	}
	
	@ApiOperation("Editar um livro")
	@RequestMapping(value = "/{id}",method = RequestMethod.PUT)
	public ResponseEntity<Void> atualizar( 
			@ApiParam(value = "ID de um livro", example = "1")
			@PathVariable("id") Long id,
			@ApiParam(name = "corpo", value = "Representação de um livro com os novos dados")
			@RequestBody Livro livro) {
		livro.setId(id);
		livrosService.atualizar(livro);
		return ResponseEntity.noContent().build();
	}
	
	@ApiOperation("Incluir comentário")
	@RequestMapping(value = "/{id}/comentarios", method = RequestMethod.POST)
	public ResponseEntity<Void> adicionarComentario(
			@ApiParam(value = "ID de um livro", example = "1")
			@PathVariable("id") Long livroId, 
			@RequestBody Comentario comentario) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		comentario.setUsuario(auth.getName());
		
		livrosService.salvarComentario(livroId, comentario);
		
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
		
		return ResponseEntity.created(uri).build();
	}

	@ApiOperation("Listar comentário por id")
	@RequestMapping(value = "/{id}/comentarios", method = RequestMethod.GET)
	public ResponseEntity<List<Comentario>> listarComentarios(@PathVariable("id") Long livroId) {
		List<Comentario> comentarios = livrosService.listarComentarios(livroId);
		
		return ResponseEntity.status(HttpStatus.OK).body(comentarios);
	}
	
}
