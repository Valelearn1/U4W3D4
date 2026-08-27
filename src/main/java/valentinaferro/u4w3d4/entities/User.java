package valentinaferro.u4w3d4.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Entity
@Table(name = "users")
@ToString
@Setter
// implements UserDetails: così Spring Security sa "leggere" questa entità come un utente autenticato.
// Da qui ricava password e authorities (i ruoli) da mettere nel SecurityContext.
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE) // id immutabile
	private Long id;

	private String nome;

	@Column(nullable = false, unique = true) // vincolo a livello di DB: email non nulla e univoca
	private String email;

	private String password;

	// EnumType.STRING salva il nome dell'enum ("USER"/"ADMIN") invece dell'indice numerico:
	// più leggibile nel DB e non si rompe se in futuro cambia l'ordine dei valori dell'enum.
	@Enumerated(EnumType.STRING)
	// Il ruolo parte come USER alla creazione (vedi costruttore); il setter serve solo per la
	// promozione ad ADMIN, esposta da un endpoint riservato agli ADMIN (UserService.updateRole).
	private Ruolo ruolo;

	public User() {
	}

	public User(String nome, String email, String password) {
		this.nome = nome;
		this.email = email;
		this.password = password;
		this.ruolo = Ruolo.USER; // ogni nuovo utente parte come USER semplice
	}

	// --- Metodi richiesti da UserDetails ---

	// Le "authorities" sono i permessi dell'utente. Qui c'è un solo ruolo per utente.
	// PREFISSO "ROLE_" OBBLIGATORIO: hasRole('ADMIN') in @PreAuthorize cerca l'authority "ROLE_ADMIN".
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + this.ruolo.name()));
	}

	// getPassword() è già generato da Lombok (@Getter) e va bene così com'è.

	// Per Spring Security lo "username" è l'identificatore univoco di login: qui è l'email.
	@Override
	public String getUsername() {
		return this.email;
	}

	// I 4 flag isAccountNonExpired / isAccountNonLocked / isCredentialsNonExpired / isEnabled
	// hanno già implementazione di default (return true) nell'interfaccia UserDetails: non li
	// riscriviamo perché non gestiamo scadenza/blocco account in questo progetto.
}
