package valentinaferro.u4w3d4.repositories;

import valentinaferro.u4w3d4.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

	// Optional evita di restituire null quando l'email non esiste: chi chiama è costretto
	// a gestire esplicitamente il caso "assente" (es. con orElseThrow / ifPresent)
	Optional<User> findByEmail(String email);

}
