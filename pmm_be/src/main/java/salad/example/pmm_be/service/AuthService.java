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
            return null;
        }
        String hash = encoder.encode(rawPassword);


        Integer userId = users.insertUser(email, hash, displayName);

        if (userId == null) return null; // ถ้า Insert ไม่สำเร็จ


        Map<String, Object> params = Map.of("userId", userId);


        String insertCat1 = "INSERT INTO categories (user_id, category_name, category_type, color_hex) VALUES (:userId, 'Food', 'Expense', '#000000')";
        String insertCat2 = "INSERT INTO categories (user_id, category_name, category_type, color_hex) VALUES (:userId, 'Work', 'Expense', '#FFFFFF')";
        String insertCat3 = "INSERT INTO categories (user_id, category_name, category_type, color_hex) VALUES (:userId, 'Game', 'Expense', '#AAAAAA')";

        jdbc.update(insertCat1, params);
        jdbc.update(insertCat2, params);
        jdbc.update(insertCat3, params);


        String insertPm1 = "INSERT INTO payment_methods (user_id, payment_method_name, color_hex) VALUES (:userId, 'Cash', '#000000')";
        String insertPm2 = "INSERT INTO payment_methods (user_id, payment_method_name, color_hex) VALUES (:userId, 'Visa', '#FFFFFF')";
        String insertPm3 = "INSERT INTO payment_methods (user_id, payment_method_name, color_hex) VALUES (:userId, 'Mastercard', '#AAAAAA')";

        jdbc.update(insertPm1, params);
        jdbc.update(insertPm2, params);
        jdbc.update(insertPm3, params);


        String insertWallet1 = "INSERT INTO wallets (user_id, wallet_name, wallet_type, color_hex) VALUES (:userId, 'Wallet_1', 'Cash', '#000000')";

        jdbc.update(insertWallet1, params);

        // 3. ส่ง userId กลับ (ใช้ตัวแปรที่ได้มา)
        // นี่คือจุดที่แก้บั๊กที่ 2 (บั๊ก Insert user ซ้ำซ้อน)
        return userId;
    }

    public UserRepository.UserAuthRow login(String email, String rawPassword) {
        var rowOpt = users.findAuthByEmail(email);
        if (rowOpt.isEmpty()) return null;                       // Cant Find email in DB

        var row = rowOpt.get();
        boolean ok = encoder.matches(rawPassword, row.passwordHash());
        // คืนค่า row (ซึ่งมี userId และ displayName) ถ้า password ถูก
        return ok ? row : null;
    }

}
