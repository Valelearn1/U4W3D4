package valentinaferro.u4w3d1.repositories;

import valentinaferro.u4w3d1.entities.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// JpaRepository<BlogPost, Long>: fornisce già gratis i metodi CRUD (save, findById, findAll,
// delete, ...) senza scrivere alcuna implementazione: Spring Data JPA la genera a runtime.
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

	// Derived query: Spring Data genera la query solo dal nome del metodo (findBy + campo + True/False).
	// Sostituisce il filtro con gli stream usato ieri.
	List<BlogPost> findByPubblicatoTrue();

	List<BlogPost> findByPubblicatoFalse();

	// Derived query: tutti i BlogPost di un determinato autore (naviga la relazione autore.id)
	List<BlogPost> findByAutoreId(Long autoreId);

	// Derived query di cancellazione massiva
	void deleteByAutoreId(Long autoreId);

	// Quando il nome del metodo non basta si scrive la query a mano in JPQL (opera su entità, non su tabelle SQL).
	// LIKE %:keyword%: BlogPost il cui titolo contiene la keyword passata come parametro
	@Query("SELECT b FROM BlogPost b WHERE b.titolo LIKE %:keyword%")
	List<BlogPost> findByTitoloContaining(@Param("keyword") String keyword);

}
