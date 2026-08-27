package valentinaferro.u4w3d4.auth;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // non è una classe di configurazione per Beans qualsiasi, ma è quella che Spring Security deve usare per configurarsi
@EnableMethodSecurity // ATTIVA @PreAuthorize / @PostAuthorize sui metodi (di default prePostEnabled = true)
public class SecurityConfig {

    // NB: AuthFilter arriva come PARAMETRO del metodo, non nel costruttore di SecurityConfig.
    // Motivo: AuthFilter -> UserService -> PasswordEncoder, e PasswordEncoder è un @Bean definito
    // QUI dentro. Iniettandolo nel costruttore si creerebbe una dipendenza circolare all'avvio;
    // come parametro di un @Bean method invece viene risolto "a chiamata", quando SecurityConfig
    // è già pronta, e il ciclo non si forma.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthFilter authFilter) throws Exception {
        // Questo disabilita il form di login che c'è di default
        httpSecurity.formLogin(formLogin -> formLogin.disable());

        // Questo disabilita le protezioni verso CSRF che quando usiamo l'autenticazione basata su token JWT sono inutili.
        httpSecurity.csrf(csrf -> csrf.disable());

        // Disabilitiamo le sessioni. Per definizione JWT è un meccanismo SENZA SESSIONI (Stateless) quindi dobbiamo disabilitarle
        httpSecurity.sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Siccome di default spring security mi torna 401 su TUTTI GLI ENDPOINT, tolgo questo controllo (che verrà rimpiazzato dal mio filtro custom)
        httpSecurity.authorizeHttpRequests(req -> req.requestMatchers("/**").permitAll());

        // Aggancio il filtro JWT PRIMA del filtro standard di username/password:
        // così quando la richiesta arriva al controller il SecurityContext è già popolato
        // con l'utente autenticato e i suoi ruoli, e @PreAuthorize ha su cosa lavorare.
        httpSecurity.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    // AuthFilter è un @Component che estende OncePerRequestFilter: Spring Boot lo registrerebbe
    // AUTOMATICAMENTE anche nella catena di filtri del servlet container (fuori da Spring Security).
    // Con questo bean disattivo quella registrazione automatica: il filtro deve girare SOLO dove
    // l'ho messo io, cioè dentro la SecurityFilterChain (addFilterBefore qui sopra).
    @Bean
    public FilterRegistrationBean<AuthFilter> disableAutoRegistrationOfAuthFilter(AuthFilter filter) {
        FilterRegistrationBean<AuthFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    // Bean unico e condiviso per l'hashing: BCrypt genera un salt casuale per ogni password
    // e lo memorizza dentro l'hash stesso. encode() per cifrare, matches(raw, hash) per verificare.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
