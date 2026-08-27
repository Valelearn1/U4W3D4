package valentinaferro.u4w3d1.controllers;

import valentinaferro.u4w3d1.dto.UserRequestDTO;
import valentinaferro.u4w3d1.dto.UserResponseDTO;
import valentinaferro.u4w3d1.exceptions.ValidationException;
import valentinaferro.u4w3d1.services.UserService;
import org.springframework.http.HttpStatus;
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
		return userService.findById(id); // 200
	}

	@GetMapping
	public List<UserResponseDTO> findAll() {
		return userService.findAll(); // 200
	}

	@PutMapping("/{id}")
	public UserResponseDTO update(@PathVariable Long id, @RequestBody @Validated UserRequestDTO userRequestDTO, BindingResult validationResult) {
		if (validationResult.hasErrors()) {
			List<String> errorsList = validationResult.getFieldErrors().stream()
					.map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
					.toList();
			throw new ValidationException(errorsList);
		}
		return userService.update(id, userRequestDTO); // 200
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204
	public void delete(@PathVariable Long id) {
		userService.delete(id);
	}

}
