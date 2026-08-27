package valentinaferro.u4w3d1.exceptions;

// Eccezione custom per risorse non trovate (es. id inesistente in una findById).
// Estende RuntimeException -> è "unchecked", non obbliga a dichiarare "throws" nei metodi che la lanciano.
public class NotFoundException extends RuntimeException {

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(Long id) {
		super("Nessuna risorsa trovata con id " + id);
	}

}
