package valentinaferro.u4w3d1.entities;

// Enum: insieme fisso di valori possibili per il ruolo di uno User.
// Salvato nel DB come stringa grazie a @Enumerated(EnumType.STRING) in User (vedi User.java).
public enum Ruolo {
	USER,
	ADMIN
}
