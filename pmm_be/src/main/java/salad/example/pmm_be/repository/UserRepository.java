package salad.example.pmm_be.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public boolean existsByEmail(String email) {
        Integer cnt = jdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email = ?",
                Integer.class, email
        );
        return cnt != null && cnt > 0;
    }

    // สมัครสมาชิก: insert แล้วคืน user_id
    public Integer insertUser(String email, String passwordHash, String displayName) {
        return jdbc.queryForObject(
                """
                INSERT INTO users (email, password_hash, display_name, created_at, updated_at)
                VALUES (?, ?, ?, now(), now())
                RETURNING user_id
                """,
                Integer.class,
                email, passwordHash, displayName
        );
    }

    public Optional<Integer> findUserIdByEmail(String email) {
        try {
            Integer id = jdbc.queryForObject(
                    "SELECT user_id FROM users WHERE email = ?",
                    Integer.class, email
            );
            return Optional.ofNullable(id);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<UserAuthRow> findAuthByEmail(String email) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject(
                            "SELECT user_id, password_hash, display_name FROM users WHERE email = ?",
                            (rs, n) -> new UserAuthRow(rs.getInt("user_id"), rs.getString("password_hash"),rs.getString("display_name")),
                            email
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    public record UserAuthRow(Integer userId, String passwordHash, String displayName) {}

}
