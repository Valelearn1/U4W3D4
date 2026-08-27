package valentinaferro.u4w3d4.controllers;

import valentinaferro.u4w3d4.dto.BlogPostRequestDTO;
import valentinaferro.u4w3d4.dto.BlogPostResponseDTO;
import valentinaferro.u4w3d4.entities.User;
import valentinaferro.u4w3d4.exceptions.ValidationException;
import valentinaferro.u4w3d4.services.BlogPostService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController = @Controller + @ResponseBody: ogni metodo restituisce direttamente il body
// della risposta (JSON), non il nome di una view HTML.
// @RequestMapping definisce il prefisso comune a tutte le rotte di questo controller.
@RestController
@RequestMapping("/api/blogposts")
public class BlogPostController {

	private final BlogPostService blogPostService;

	// Dependency Injection via costruttore: Spring inietta automaticamente il bean BlogPostService
	public BlogPostController(BlogPostService blogPostService) {
		this.blogPostService = blogPostService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED) // 201: risorsa creata con successo
	// @Validated attiva i controlli definiti dalle annotazioni nel DTO (es. @NotBlank).
	// BindingResult deve stare SUBITO dopo l'oggetto validato: se ci sono errori li raccoglie
	// lì invece di far esplodere subito una eccezione di Spring.
	public BlogPostResponseDTO create(@RequestBody @Validated BlogPostRequestDTO blogPostRequestDTO, BindingResult validationResult) {
		if (validationResult.hasErrors()) {
			// Trasformo la lista di errori di Spring in una lista di stringhe leggibili "campo: messaggio"
			List<String> errorsList = validationResult.getFieldErrors().stream()
					.map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
					.toList();
			throw new ValidationException(errorsList); // gestita dal GlobalExceptionHandler -> 400
		}
		return blogPostService.create(blogPostRequestDTO);
	}

	@GetMapping("/{id}")
	// @PathVariable estrae {id} dall'URL e lo passa come parametro del metodo
	public BlogPostResponseDTO findById(@PathVariable Long id) {
		return blogPostService.findById(id); // 200
	}

	@GetMapping
	// @RequestParam(required = false): query param opzionali, es. /api/blogposts?pubblicato=true&titolo=java
	public List<BlogPostResponseDTO> findAll(@RequestParam(required = false) String pubblicato,
	                                         @RequestParam(required = false) String titolo) {
		return blogPostService.findAll(pubblicato, titolo);
	}

	@PutMapping("/{id}")
	// @AuthenticationPrincipal inietta l'utente autenticato (il principal messo nel SecurityContext
	// da AuthFilter): qui è la nostra entità User. Serve al service per controllare che sia l'autore.
	public BlogPostResponseDTO update(@PathVariable Long id, @RequestBody @Validated BlogPostRequestDTO blogPostRequestDTO, BindingResult validationResult,
	                                  @AuthenticationPrincipal User currentUser) {
		if (validationResult.hasErrors()) {
			List<String> errorsList = validationResult.getFieldErrors().stream()
					.map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
					.toList();
			throw new ValidationException(errorsList);
		}
		return blogPostService.update(id, blogPostRequestDTO, currentUser); // 200
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204: successo, nessun contenuto da restituire
	public void delete(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
		blogPostService.delete(id, currentUser);
	}

	// [EXTRA] tutti i BlogPost scritti da un determinato Author
	@GetMapping("/autore/{autoreId}")
	public List<BlogPostResponseDTO> findByAutore(@PathVariable Long autoreId) {
		return blogPostService.findByAutoreId(autoreId);
	}


}
