package valentinaferro.u4w3d4.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import valentinaferro.u4w3d4.entities.User;
import valentinaferro.u4w3d4.exceptions.UnauthorizedException;

import java.util.Date;

@Component
public class JWTTools {
    @Value("${spring.jet.secret}") String secret;

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(String.valueOf(user.getId())) // subject, cioè a chi appartiene il token (ID DELL'UTENTE) N.B. NO DATI SENSIBILI!!!
                .issuedAt(new Date(System.currentTimeMillis())) // IssuedAt (IaT) cioè data di emissione del token, va messa in millisecondi
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // Expiration, cioè data di scadenza, va messa in millisecondi
                .signWith(Keys.hmacShaKeyFor(secret.getBytes())) // Firmiamo il token (con l'algoritmo HMAC-SHA e il SECRET contenuto in application.properties) per l'integrità del token
                .compact(); // Prende tutte le info di sopra e crea il token
    }

    public void verifyToken(String token) {
        try {
            Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes())).build().parse(token);
        } catch (Exception ex) {
            throw new UnauthorizedException("Token invalido, rifare il login!");
        }
    }

    // extractIdFromToken: rilegge il "subject" del token (in generateToken ci abbiamo messo l'ID
    // dell'utente) e lo converte in long. parseSignedClaims verifica di nuovo firma e scadenza,
    // poi getPayload().getSubject() restituisce l'ID come stringa. Lo usa il filtro per sapere
    // QUALE utente sta facendo la richiesta e caricarlo dal DB.
    public long extractIdFromToken(String token) {
        return Long.parseLong(Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes())).build().parseSignedClaims(token).getPayload().getSubject());
    }


}
