package valentinaferro.u4w3d4.controllers;

import valentinaferro.u4w3d4.dto.AssignRoleDTO;
import valentinaferro.u4w3d4.dto.UserRequestDTO;
import valentinaferro.u4w3d4.dto.UserResponseDTO;
import valentinaferro.u4w3d4.entities.User;
import valentinaferro.u4w3d4.exceptions.ValidationException;
import valentinaferro.u4w3d4.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED) // 201
	// @Validated + BindingResult: se il body non rispetta i vincoli del DTO (es. email non valida
	// o password troppo debole), Spring popola BindingResult invece di lanciare subito un'eccezione,
	// e tocca a noi controllarlo esplicitamente.
	public UserResponseDTO create(@RequestBody @Validated UserRequestDTO userRequestDTO, BindingResult validationResult) {
		if (validationResult.hasErrors()) {
			List<String> errorsList = validationResult.getFieldErrors().stream()
					.map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
					.toList();
			throw new ValidationException(errorsList);
		}
		return userService.create(userRequestDTO);
	}

	@GetMapping("/{id}")
	public UserResponseDTO findById(@PathVariable Long id) {
		return userService.findByIdResponse(id); // 200 (findById ora ritorna l'entità: qui serve il DTO)
	}

	@GetMapping
	// AUTORIZZAZIONE: solo un ADMIN può vedere l'elenco completo degli utenti registrati.
	// @PreAuthorize viene valutata PRIMA di entrare nel metodo, sulla base di ciò che AuthFilter
	// ha messo nel SecurityContext. "hasRole('ADMIN')" cerca l'authority "ROLE_ADMIN".
	@PreAuthorize("hasRole('ADMIN')")
	public List<UserResponseDTO> findAll() {
		return userService.findAll(); // 200
	}

	@PutMapping("/{id}")
	// @AuthenticationPrincipal: l'utente autenticato messo nel SecurityContext da AuthFilter.
	// Serve al service per verificare che stia modificando il proprio profilo (o che sia ADMIN).
	public UserResponseDTO update(@PathVariable Long id, @RequestBody @Validated UserRequestDTO userRequestDTO, BindingResult validationResult,
	                             @AuthenticationPrincipal User currentUser) {
		if (validationResult.hasErrors()) {
			List<String> errorsList = validationResult.getFieldErrors().stream()
					.map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
					.toList();
			throw new ValidationException(errorsList);
		}
		return userService.update(id, userRequestDTO, currentUser); // 200
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204
	public void delete(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
		userService.delete(id, currentUser);
	}

	// PATCH /api/users/{id}/role : promuove/cambia il ruolo di un utente (es. da USER ad ADMIN).
	// Riservato agli ADMIN. Body: { "ruolo": "ADMIN" }
	@PatchMapping("/{id}/role")
	@PreAuthorize("hasRole('ADMIN')")
	public UserResponseDTO updateRole(@PathVariable Long id, @RequestBody @Validated AssignRoleDTO body, BindingResult validationResult) {
		if (validationResult.hasErrors()) {
			List<String> errorsList = validationResult.getFieldErrors().stream()
					.map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
					.toList();
			throw new ValidationException(errorsList);
		}
		return userService.updateRole(id, body); // 200
	}

}
