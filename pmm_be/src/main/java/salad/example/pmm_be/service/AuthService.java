package salad.example.pmm_be.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import salad.example.pmm_be.repository.UserRepository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;

@Service
public class AuthService {
    private final UserRepository users;
    private final NamedParameterJdbcTemplate jdbc;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository users, NamedParameterJdbcTemplate jdbc) {
        this.users = users;
        this.jdbc = jdbc;
    }

    public Integer register(String email, String rawPassword, String displayName) {
        if (users.existsByEmail(email)) {
            return null; // Email are the same with DB(See in UserRepository)
        }
        String hash = encoder.encode(rawPassword);
        Integer userId = users.insertUser(email, hash, displayName);

        if (userId == null) return null;

        // 3) สร้าง Default Data (PostgreSQL)
        Map<String, Object> params = Map.of("userId", userId);

        String insertCategories = """
            INSERT INTO categories (user_id, category_name, category_type, color_hex)
            SELECT :userId, v.name, v.type, v.color
            FROM (VALUES
                ('Food', 'Expense', '#000000'),
                ('Work', 'Expense', '#FFFFFF'),
                ('Game', 'Expense', '#AAAAAA')
            ) AS v(name, type, color)
            ON CONFLICT (user_id, category_name, category_type) DO NOTHING;
        """;

        String insertPaymentMethods = """
            INSERT INTO payment_methods (user_id, payment_method_name, color_hex)
            SELECT :userId, v.name, v.color
            FROM (VALUES
                ('Cash', '#000000'),
                ('Visa', '#FFFFFF'),
                ('Mastercard', '#AAAAAA')
            ) AS v(name, color)
            ON CONFLICT (user_id, payment_method_name) DO NOTHING;
        """;

        String insertWallets = """
            INSERT INTO wallets (user_id, wallet_name, wallet_type, color_hex)
            SELECT :userId, v.name, v.type, v.color
            FROM (VALUES
                ('Wallet_1', 'Cash', '#000000')
            ) AS v(name, type, color)
            ON CONFLICT (user_id, wallet_name) DO NOTHING;
        """;

        jdbc.update(insertCategories, params);
        jdbc.update(insertPaymentMethods, params);
        jdbc.update(insertWallets, params);

        // 4) ส่ง userId กลับ
        return users.insertUser(email, hash, displayName);
    }

    public Integer login(String email, String rawPassword) {
        var rowOpt = users.findAuthByEmail(email);
        if (rowOpt.isEmpty()) return null;                       // Cant Find email in DB

        var row = rowOpt.get();
        boolean ok = encoder.matches(rawPassword, row.passwordHash());
        return ok ? row.userId() : null;
    }

}
