package valentinaferro.u4w3d4.exceptions;

import java.util.List;

// Eccezione lanciata dai controller quando la validazione del DTO (BindingResult) fallisce.
// Porta con sé la lista di TUTTI gli errori di validazione, non solo il primo trovato.
public class ValidationException extends RuntimeException {

	private List<String> errorsList;

	public ValidationException(List<String> errorsList) {
		super("Errori di validazione");
		this.errorsList = errorsList;
	}

	public List<String> getErrorsList() {
		return errorsList;
	}

}
