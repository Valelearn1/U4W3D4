package valentinaferro.u4w3d4.payloads;

import java.time.LocalDateTime;

// record: classe immutabile generata automaticamente dal compilatore con costruttore,
// getter (timestamp(), message()), equals/hashCode/toString -> ideale per DTO che trasportano
// solo dati, senza logica. Usato come corpo JSON quando si verifica un errore generico/404/duplicato.
public record ErrorResponse(
		LocalDateTime timestamp,
		String message
) {
}
