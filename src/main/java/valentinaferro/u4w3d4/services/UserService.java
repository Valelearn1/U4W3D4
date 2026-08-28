package valentinaferro.u4w3d4.services;

import valentinaferro.u4w3d4.dto.AssignRoleDTO;
import valentinaferro.u4w3d4.dto.UserRequestDTO;
import valentinaferro.u4w3d4.dto.UserResponseDTO;
import valentinaferro.u4w3d4.entities.Ruolo;
import valentinaferro.u4w3d4.entities.User;
import valentinaferro.u4w3d4.exceptions.DuplicateEmailException;
import valentinaferro.u4w3d4.exceptions.NotFoundException;
import valentinaferro.u4w3d4.exceptions.ValidationException;
import valentinaferro.u4w3d4.repositories.BlogPostRepository;
import valentinaferro.u4w3d4.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final BlogPostRepository blogPostRepository; // serve solo per cancellare a cascata i post in delete()
	private final PasswordEncoder bcrypt; // il bean BCryptPasswordEncoder definito in SecurityConfig

	public UserService(UserRepository userRepository, BlogPostRepository blogPostRepository, PasswordEncoder bcrypt) {
		this.userRepository = userRepository;
		this.blogPostRepository = blogPostRepository;
		this.bcrypt = bcrypt;
	}

	public UserResponseDTO create(UserRequestDTO dto) {
		// Controllo applicativo di unicità email (oltre al vincolo @Column(unique = true) sul DB):
		// così possiamo restituire un errore leggibile invece di una generica eccezione SQL.
		userRepository.findByEmail(dto.email()).ifPresent(u -> {
			throw new DuplicateEmailException(dto.email());
		});

		// HASHING: la password che arriva dal client NON viene mai salvata in chiaro.
		// bcrypt.encode() applica BCrypt (hash + salt casuale) e restituisce la stringa da mettere nel DB.
		User nuovoUser = new User(dto.nome(), dto.email(), bcrypt.encode(dto.password()));
		User salvato = userRepository.save(nuovoUser);
		return toResponseDTO(salvato);
	}

	// Versione per il controller: mappa l'entità sul DTO di risposta (che NON contiene la password)
	public UserResponseDTO findByIdResponse(Long id) {
		return toResponseDTO(findById(id));
	}

	// Restituisce l'entità User (non il DTO): usata dal filtro di autenticazione e da altri
	// service (es. BlogPostService) che hanno bisogno dell'oggetto User vero e proprio.
	public User findById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Nessuno User trovato con id " + id));
	}

	public List<UserResponseDTO> findAll() {
		return userRepository.findAll().stream()
				.map(this::toResponseDTO)
				.toList();
	}

	public UserResponseDTO update(Long id, UserRequestDTO dto, User currentUser) {
		User esistente = findById(id);

		// AUTORIZZAZIONE: puoi modificare SOLO il tuo profilo, a meno che tu non sia ADMIN.
		// Senza questo controllo qualsiasi utente autenticato potrebbe cambiare nome/email/password
		// di un altro utente qualsiasi passando il suo id nell'URL (IDOR).
		if (currentUser.getRuolo() != Ruolo.ADMIN && !currentUser.getId().equals(id)) {
			throw new AccessDeniedException("Puoi modificare solo il tuo profilo");
		}

		// Ricontrollo l'unicità dell'email solo se l'utente la sta effettivamente cambiando,
		// altrimenti la query troverebbe sempre se stesso e lancerebbe l'eccezione per errore.
		if (!esistente.getEmail().equals(dto.email())) {
			userRepository.findByEmail(dto.email()).ifPresent(u -> {
				throw new DuplicateEmailException(dto.email());
			});
		}

		esistente.setNome(dto.nome());
		esistente.setEmail(dto.email());
		// Anche in update la password va rihashata prima del salvataggio: mai in chiaro nel DB.
		esistente.setPassword(bcrypt.encode(dto.password()));

		return toResponseDTO(userRepository.save(esistente));
	}

	// Assegnazione ruolo: serve per promuovere un utente ad ADMIN. L'endpoint sarà riservato agli ADMIN.
	public UserResponseDTO updateRole(Long id, AssignRoleDTO body) {
		User esistente = findById(id);

		// body.ruolo() è una STRINGA arrivata dal client (es. "ADMIN"). Va convertita nel valore
		// dell'enum Ruolo. Ruolo.valueOf(s) fa questa conversione, ma è un metodo "rigido":
		//   - accetta SOLO stringhe che coincidono ESATTAMENTE con il nome di un valore dell'enum
		//     ("USER" o "ADMIN"), maiuscole/minuscole comprese -> per questo faccio prima .toUpperCase();
		//   - se la stringa non corrisponde a nessun valore (es. "superadmin", "", "Admin ") lancia
		//     IllegalArgumentException, che è una eccezione STANDARD di Java (java.lang, unchecked:
		//     non obbliga a dichiararla né a gestirla).
		// Senza try/catch quella IllegalArgumentException risalirebbe fino al GlobalExceptionHandler
		// e finirebbe nel ramo generico -> risposta 500 (errore del server), che è FUORVIANTE:
		// non è un bug del server, è l'utente che ha mandato un dato sbagliato.
		// Quindi la catturo e la ri-lancio come ValidationException, che il GlobalExceptionHandler
		// mappa su 400 Bad Request con un messaggio chiaro.
		try {
			esistente.setRuolo(Ruolo.valueOf(body.ruolo().toUpperCase()));
		} catch (IllegalArgumentException ex) {
			throw new ValidationException(List.of("Ruolo non valido: '" + body.ruolo() + "'. Valori ammessi: USER, ADMIN"));
		}

		return toResponseDTO(userRepository.save(esistente));
	}


	@Transactional // Quando più di una modifica al DB avviene nello stesso metodo è OBBLIGATORIO usare Transactional
	public void delete(Long id, User currentUser) {
		User esistente = findById(id);

		// AUTORIZZAZIONE: puoi cancellare SOLO il tuo profilo, a meno che tu non sia ADMIN.
		if (currentUser.getRuolo() != Ruolo.ADMIN && !currentUser.getId().equals(id)) {
			throw new AccessDeniedException("Puoi cancellare solo il tuo profilo");
		}

		blogPostRepository.deleteByAutoreId(id); // cancella prima i post collegati (altrimenti violazione FK)
		userRepository.delete(esistente);
	}

	// Mapping unico Entity -> DTO: un solo punto da cambiare se il DTO evolve
	private UserResponseDTO toResponseDTO(User user) {
		return new UserResponseDTO(user.getId(), user.getNome(), user.getEmail(), user.getRuolo());
	}

}
