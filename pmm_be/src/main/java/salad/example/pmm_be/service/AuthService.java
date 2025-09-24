package salad.example.pmm_be.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import salad.example.pmm_be.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository users;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository users) { this.users = users; }

    public Integer register(String email, String rawPassword, String displayName) {
        if (users.existsByEmail(email)) {
            return null; // Email are the same with DB(See in UserRepository)
        }
        String hash = encoder.encode(rawPassword);
        return users.insertUser(email, hash, displayName);
    }
}
