package valentinaferro.u4w3d1.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import valentinaferro.u4w3d1.auth.JWTTools;
import valentinaferro.u4w3d1.dto.LoginRequestDTO;
import valentinaferro.u4w3d1.entities.User;
import valentinaferro.u4w3d1.exceptions.UnauthorizedException;
import valentinaferro.u4w3d1.repositories.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JWTTools jwtTools;
    private final PasswordEncoder bcrypt; // stesso bean usato in UserService per l'hashing

    public AuthService(UserRepository userRepository, JWTTools jwtTools, PasswordEncoder bcrypt) {
        this.userRepository = userRepository;
        this.jwtTools = jwtTools;
        this.bcrypt = bcrypt;
    }

    public String checkCredentialsAndGenerateToken(LoginRequestDTO body) {
        User fromDB = userRepository.findByEmail(body.email())
                .orElseThrow(() -> new UnauthorizedException("Credenziali errate"));

        // NON si può più fare un confronto diretto fra stringhe: nel DB c'è l'HASH, non la password.
        // bcrypt.matches(passwordInChiaro, hashSalvato) ri-applica BCrypt alla password ricevuta
        // (usando il salt contenuto nell'hash) e confronta il risultato. true = password corretta.
        if (!bcrypt.matches(body.password(), fromDB.getPassword()))
            throw new UnauthorizedException("Credenziali errate");

        return jwtTools.generateToken(fromDB);
    }
}
