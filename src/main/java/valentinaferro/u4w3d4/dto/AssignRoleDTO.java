package valentinaferro.u4w3d4.dto;

import jakarta.validation.constraints.NotBlank;

// Body di PATCH /api/users/{id}/role: contiene solo il nuovo ruolo da assegnare, come stringa.
// La conversione stringa -> enum Ruolo (e la sua validazione) avviene in UserService.updateRole.
public record AssignRoleDTO(

		@NotBlank(message = "Il ruolo è obbligatorio")
		String ruolo
) {
}
