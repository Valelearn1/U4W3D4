package valentinaferro.u4w3d4.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// DTO "in ingresso": rappresenta il body JSON accettato da POST/PUT /api/blogposts.
// Le annotazioni di jakarta.validation vengono controllate automaticamente da @Validated nel controller.
public record BlogPostRequestDTO(

		@NotBlank(message = "La categoria è obbligatoria")
		String categoria,

		@NotBlank(message = "Il titolo è obbligatorio")
		String titolo,

		@NotBlank(message = "Il contenuto è obbligatorio")
		String contenuto,

		@Min(value = 0, message = "Il tempo di lettura non può essere negativo")
		int tempoDiLettura,

		boolean pubblicato,

		@NotNull(message = "L'id dell'autore è obbligatorio")
		Long autoreId

) {
}
