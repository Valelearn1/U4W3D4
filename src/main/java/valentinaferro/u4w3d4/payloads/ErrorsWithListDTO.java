package valentinaferro.u4w3d4.payloads;

import java.time.LocalDateTime;
import java.util.List;

// Come ErrorResponse, ma pensato per gli errori di validazione: porta anche
// la lista dettagliata di tutti i campi non validi (vedi ValidationException).
public record ErrorsWithListDTO(
		String message,
		LocalDateTime timestamp,
		List<String> errorsList
) {
}
