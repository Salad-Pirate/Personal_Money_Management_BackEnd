package salad.example.pmm_be.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Email @NotBlank String email,
        @Size(min = 8, message = "password must be at least 8 characters")
        String password,
        @NotBlank String displayName
) {}

