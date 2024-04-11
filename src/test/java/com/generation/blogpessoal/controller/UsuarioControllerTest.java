package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@BeforeAll
	void start() {
		usuarioRepository.deleteAll();

		usuarioService.cadastrarUsuario(new Usuario(0l, "Root", "root-root@root.com", "rootroot", " "));
	}

	@Test
	@DisplayName("Cadastrar um Usuário")
	public void deveCriarUmUsuario() {
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(
				new Usuario(0L, "Sakura Haruno", "harunosakura@email.com", "22223333", "-"));

		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST,
				corpoRequisicao, Usuario.class);

		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	}

	@Test
	@DisplayName("Não deve permitir a duplicação do Usuário")
	public void naoDuplicarUsuario() {

		usuarioService.cadastrarUsuario(new Usuario(0L, "Naruto Uzumaki", "uzumakinaruto@email.com", "33334444", "-"));

		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(
				new Usuario(0L, "Naruto Uzumaki", "uzumakinaruto@email.com", "33334444", "-"));

		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST,corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	}

	@Test
	@DisplayName("Atualizar Usuário")
	public void deveAtualizarUmUsuario() {
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario
				(0L, "Sasuke Uchiha 1", "uchihasasuke1@email.com", "sasuke123", "-"));
		
		Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(),
				"Sasuke Uchiha","uchihasasuke@email.com", "sasuke123", "-");
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.withBasicAuth("root-root@root.com", "rootroot")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Listar todos os Usuários")
	public void mostrarTodosUsuarios() {
		usuarioService.cadastrarUsuario(new Usuario(0L, "Itachi Uchiha", "uchihaitachi@email.com", "itachi123", "-"));
		
		usuarioService.cadastrarUsuario(new Usuario(0L, "Tomura Shigaraki", "shigarakitomura@email.com", "shigaraki123", "-"));
		
		ResponseEntity <String> resposta = testRestTemplate
				.withBasicAuth("root-root@root.com", "rootroot")
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	
}
