package valentinaferro.u4w3d1.services;

import valentinaferro.u4w3d1.dto.BlogPostRequestDTO;
import valentinaferro.u4w3d1.dto.BlogPostResponseDTO;
import valentinaferro.u4w3d1.dto.UserResponseDTO;
import valentinaferro.u4w3d1.entities.BlogPost;
import valentinaferro.u4w3d1.entities.Ruolo;
import valentinaferro.u4w3d1.entities.User;
import valentinaferro.u4w3d1.exceptions.NotFoundException;
import valentinaferro.u4w3d1.repositories.BlogPostRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

// @Service: bean gestito da Spring che contiene la logica di business,
// separata dal controller (che si occupa solo di HTTP) e dal repository (che si occupa solo di DB).
@Service
public class BlogPostService {

	private final BlogPostRepository blogPostRepository;
	private final UserService userService; // serve per recuperare/validare l'autore del post

	public BlogPostService(BlogPostRepository blogPostRepository, UserService userService) {
		this.blogPostRepository = blogPostRepository;
		this.userService = userService;
	}

	public BlogPostResponseDTO create(BlogPostRequestDTO dto) {
		// Verifico che l'autore esista davvero prima di creare il post (altrimenti NotFoundException -> 404)
		User autore = userService.findEntityById(dto.autoreId());

		BlogPost nuovoPost = new BlogPost(
				dto.categoria(),
				dto.titolo(),
				dto.contenuto(),
				dto.tempoDiLettura(),
				dto.pubblicato(),
				autore
		);
		BlogPost salvato = blogPostRepository.save(nuovoPost); // INSERT nel DB, salvato ha ora l'id generato

		// Mapping manuale Entity -> DTO: non esponiamo mai l'entità JPA direttamente all'esterno
		// (evita loop infiniti di serializzazione ed espone solo i campi che vogliamo mostrare)
		User autoreSalvato = salvato.getAutore();
		UserResponseDTO autoreDTO = new UserResponseDTO(autoreSalvato.getId(), autoreSalvato.getNome(), autoreSalvato.getEmail(), autoreSalvato.getRuolo());

		return new BlogPostResponseDTO(salvato.getId(),
				salvato.getCategoria(),
				salvato.getTitolo(),
				salvato.getCover(),
				salvato.getContenuto(),
				salvato.getTempoDiLettura(),
				salvato.isPubblicato(),
				autoreDTO
		);
	}

	public BlogPostResponseDTO findById(Long id) {
		BlogPost post = findEntityById(id);

		User autore = post.getAutore();
		UserResponseDTO autoreDTO = new UserResponseDTO(autore.getId(), autore.getNome(), autore.getEmail(), autore.getRuolo());

		return new BlogPostResponseDTO(
				post.getId(),
				post.getCategoria(),
				post.getTitolo(),
				post.getCover(),
				post.getContenuto(),
				post.getTempoDiLettura(),
				post.isPubblicato(),
				autoreDTO
		);
	}

	// Metodo privato riusato da findById/update/delete: centralizza il "trova o lancia 404"
	private BlogPost findEntityById(Long id) {
		return blogPostRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Nessun BlogPost trovato con id " + id));
	}

	public List<BlogPostResponseDTO> findAll(String pubblicato, String titolo) {
		List<BlogPost> risultato;

		if (titolo != null && !titolo.isBlank()) {
			risultato = blogPostRepository.findByTitoloContaining(titolo);
		} else {
			risultato = blogPostRepository.findAll();
		}

		if (pubblicato != null) {
			// Filtro applicato in memoria sul risultato già recuperato dal DB (non è una query separata)
			boolean filtro = Boolean.parseBoolean(pubblicato);
			risultato = risultato.stream()
					.filter(post -> post.isPubblicato() == filtro)
					.toList();
		}

		return risultato.stream()
				.map(post -> {
					User autore = post.getAutore();
					UserResponseDTO autoreDTO = new UserResponseDTO(autore.getId(), autore.getNome(), autore.getEmail(), autore.getRuolo());

					return new BlogPostResponseDTO(
							post.getId(),
							post.getCategoria(),
							post.getTitolo(),
							post.getCover(),
							post.getContenuto(),
							post.getTempoDiLettura(),
							post.isPubblicato(),
							autoreDTO
					);
				})
				.toList();
	}

	public List<BlogPostResponseDTO> findByAutoreId(Long autoreId) {
		userService.findEntityById(autoreId); // verifica che l'autore esista, altrimenti 404
		return blogPostRepository.findByAutoreId(autoreId).stream()
				.map(post -> {
					User autore = post.getAutore();
					UserResponseDTO autoreDTO = new UserResponseDTO(autore.getId(), autore.getNome(), autore.getEmail(), autore.getRuolo());

					return new BlogPostResponseDTO(
							post.getId(),
							post.getCategoria(),
							post.getTitolo(),
							post.getCover(),
							post.getContenuto(),
							post.getTempoDiLettura(),
							post.isPubblicato(),
							autoreDTO
					);
				})
				.toList();
	}

	public BlogPostResponseDTO update(Long id, BlogPostRequestDTO dto) {
		BlogPost esistente = findEntityById(id);

		// Aggiorno i campi dell'entità già gestita da JPA (l'autore non si cambia in fase di update)
		esistente.setCategoria(dto.categoria());
		esistente.setTitolo(dto.titolo());
		esistente.setContenuto(dto.contenuto());
		esistente.setTempoDiLettura(dto.tempoDiLettura());
		esistente.setPubblicato(dto.pubblicato());

		BlogPost salvato = blogPostRepository.save(esistente); // UPDATE (perché l'entità ha già un id)

		User autoreSalvato = salvato.getAutore();
		UserResponseDTO autoreDTO = new UserResponseDTO(autoreSalvato.getId(), autoreSalvato.getNome(), autoreSalvato.getEmail(), autoreSalvato.getRuolo());

		return new BlogPostResponseDTO(
				salvato.getId(),
				salvato.getCategoria(),
				salvato.getTitolo(),
				salvato.getCover(),
				salvato.getContenuto(),
				salvato.getTempoDiLettura(),
				salvato.isPubblicato(),
				autoreDTO
		);
	}

	public void delete(Long id) {
		BlogPost esistente = findEntityById(id);
		blogPostRepository.delete(esistente);
	}

}
