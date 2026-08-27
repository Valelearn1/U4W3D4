package valentinaferro.u4w3d4.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// DTO in ingresso per creazione/modifica di uno User.
public record UserRequestDTO(

		@NotBlank(message = "Il nome è obbligatorio")
		String nome,

		@NotBlank(message = "L'email è obbligatoria")
		@Email(message = "L'email non è valida")
		String email,

		@NotBlank(message = "La password è obbligatoria")
		// Regex: almeno una minuscola, una maiuscola, un numero, un carattere speciale,
		// minimo 8 caratteri, senza spazi.
		@Pattern(
				regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
				message = "La password deve contenere almeno 8 caratteri, una lettera maiuscola, " +
						"una minuscola, un numero e un carattere speciale (@#$%^&+=!), senza spazi"
		)
		String password
) {
}
