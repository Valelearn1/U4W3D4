package valentinaferro.u4w3d1.exceptions;

// Eccezione custom (unchecked, estende RuntimeException) lanciata quando si prova
// a registrare/modificare un'email già presente nel DB. Intercettata da GlobalExceptionHandler -> 400.
public class DuplicateEmailException extends RuntimeException {
	public DuplicateEmailException(String email) {
		super("L'email " + email + " + è già in uso");
	}
}
