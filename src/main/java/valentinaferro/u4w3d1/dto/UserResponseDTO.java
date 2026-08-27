package valentinaferro.u4w3d1.dto;

import valentinaferro.u4w3d1.entities.Ruolo;

// Non contiene mai la password: DTO di risposta usato ovunque vada esposto un autore.
public record UserResponseDTO(
		Long id,
		String nome,
		String email,
		Ruolo ruolo
) {
}
