package salad.example.pmm_be.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salad.example.pmm_be.request.RegisterRequest;
import salad.example.pmm_be.request.LoginRequest;
import salad.example.pmm_be.response.RegisterResponse;
import salad.example.pmm_be.response.LoginResponse;
import salad.example.pmm_be.service.AuthService;


@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService auth;

    public AuthController(AuthService auth) { this.auth = auth; }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        Integer userId = auth.register(req.email(), req.password(), req.displayName());
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorMessage("Email already exists"));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponse(userId, req.displayName()));
    }

    record ErrorMessage(String message) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        Integer userId = auth.login(req.email(), req.password());
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorMessage("Invalid email or password"));
        }
        return ResponseEntity.ok(new salad.example.pmm_be.response.LoginResponse(userId));
    }
}
