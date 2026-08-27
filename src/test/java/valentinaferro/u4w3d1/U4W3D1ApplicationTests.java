package valentinaferro.u4w3d1;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// @SpringBootTest fa partire l'intero contesto Spring (tutti i bean: controller, service, repository,
// connessione al DB...) esattamente come farebbe l'app vera, ma in un ambiente di test.
@SpringBootTest
class U4W3D1ApplicationTests {

    // Test "sentinella" generato automaticamente da Spring Initializr: non fa asserzioni esplicite,
    // ma se il contesto Spring NON riesce a caricarsi (es. un bean mancante, una dipendenza rotta,
    // il DB non raggiungibile) questo test fallisce con un'eccezione, segnalando il problema.
    @Test
    void contextLoads() {
    }

}
