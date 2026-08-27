package valentinaferro.u4w3d1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(

    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "L'email non è valida")
    String email,

    @NotBlank(message = "La password è obbligatoria")
    String password) {
    }
