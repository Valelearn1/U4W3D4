package valentinaferro.u4w3d1.controllers;

import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import valentinaferro.u4w3d1.dto.LoginRequestDTO;
import valentinaferro.u4w3d1.dto.LoginResponseDTO;
import valentinaferro.u4w3d1.exceptions.ValidationException;
import valentinaferro.u4w3d1.services.AuthService;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody @Validated LoginRequestDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                    .toList();
            throw new ValidationException(errorsList);
        }

        String accessToken = authService.checkCredentialsAndGenerateToken(body);
        return new LoginResponseDTO(accessToken);
    }

}
