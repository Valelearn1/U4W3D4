package valentinaferro.u4w3d4.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import valentinaferro.u4w3d4.entities.User;
import valentinaferro.u4w3d4.exceptions.UnauthorizedException;
import valentinaferro.u4w3d4.services.UserService;

import java.io.IOException;

// imposta filtro per ogni richiesta (middleware)
@Component
public class AuthFilter extends OncePerRequestFilter {

    @Autowired JWTTools jwtTools; // perché stiamo lavorando con due bean
    @Autowired UserService userService; // per ricaricare l'utente vero e proprio partendo dall'id del token

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // per controllare token a ogni richiesta HTTP
        String authToken = request.getHeader("Authorization");
        if (authToken == null || !authToken.startsWith("Bearer ")) throw new UnauthorizedException("Inserire il token nell'header"); // 401

        // decodificare il token
        String accessToken = authToken.replace("Bearer ", "");

        // controllare se il token è scaduto, se è malformato, se è stato manipolato
        jwtTools.verifyToken(accessToken); // se il token non va bene -> ERRORE

        // 1. Cerchiamo l'utente nel DB tramite id
        // 1.1 L'id al momento sta nel payload del token (il "subject")
        long currentUserId = jwtTools.extractIdFromToken(accessToken);
        // 1.2 Tramite il UserService facciamo findById
        User currentUser = this.userService.findById(currentUserId);

        // 2. Associamo l'utente corrente alla richiesta corrente inserendolo nel SecurityContext.
        //    Da qui in poi @PreAuthorize e @AuthenticationPrincipal hanno l'utente e i suoi ruoli su cui lavorare.
        Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 6. Se il token va bene andiamo avanti all'endpoint desiderato
        filterChain.doFilter(request, response); // Se dimentico questo nel filtro, al controller non ci arriviamo mai
    }

    // shouldNotFilter

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        AntPathMatcher matcher = new AntPathMatcher();
        // Rotte pubbliche: login e registrazione di un nuovo utente non possono richiedere un token
        return matcher.match("/api/auth/**", path)
                || (matcher.match("/api/users", path) && "POST".equalsIgnoreCase(request.getMethod()));
    }
}
