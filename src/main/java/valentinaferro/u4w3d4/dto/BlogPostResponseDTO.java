package valentinaferro.u4w3d4.dto;

// DTO "in uscita": quello che il client riceve nelle risposte JSON.
// Contiene l'autore già come UserResponseDTO (senza password) invece che come entità User.
public record BlogPostResponseDTO(
		Long id,
		String categoria,
		String titolo,
		String cover,
		String contenuto,
		int tempoDiLettura,
		boolean pubblicato,
		UserResponseDTO autore
) {
}
