package valentinaferro.u4w3d1.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// @Entity: questa classe è mappata su una tabella del database (JPA/Hibernate).
// @Table(name=...): nome della tabella nel DB, utile quando differisce dal nome della classe.
@Entity
@Table(name = "blogs")
@Getter // Lombok genera automaticamente tutti i getter a compile-time
@Setter // ... e tutti i setter
@ToString
public class BlogPost {

	@Id // chiave primaria
	@GeneratedValue(strategy = GenerationType.IDENTITY) // l'id è generato dal DB (auto-increment)
	@Setter(AccessLevel.NONE) // sovrascrive il @Setter di classe: l'id non deve mai essere modificabile dall'esterno
	private Long id;
	private String categoria;
	private String titolo;
	private String cover;
	private String contenuto;
	@Column(name = "tempo_lettura") // nome della colonna nel DB diverso dal nome del campo Java
	private int tempoDiLettura;
	private boolean pubblicato;

	// Relazione N:1 -> molti BlogPost per un solo User (autore)
	@ManyToOne
	@JoinColumn(name = "autore_id", nullable = false) // colonna FK nella tabella blogs
	@Setter(AccessLevel.NONE) // l'autore si imposta solo in fase di creazione, non con un setter pubblico
	private User autore;

	// Costruttore vuoto obbligatorio per JPA/Hibernate (usato internamente per istanziare l'entità)
	public BlogPost() {
	}

	public BlogPost(String categoria, String titolo, String contenuto,
	                int tempoDiLettura, boolean pubblicato, User autore) {
		this.categoria = categoria;
		this.titolo = titolo;
		this.contenuto = contenuto;
		this.tempoDiLettura = tempoDiLettura;
		this.pubblicato = pubblicato;
		this.cover = "https://picsum.photos/300/200"; // immagine di default
		this.autore = autore;
	}


}
