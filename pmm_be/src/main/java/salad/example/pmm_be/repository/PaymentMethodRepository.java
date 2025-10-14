package salad.example.pmm_be.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import salad.example.pmm_be.response.PaymentMethodResponse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class PaymentMethodRepository {

    private final JdbcTemplate jdbc;

    public PaymentMethodRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private RowMapper<PaymentMethodResponse> mapper() {
        return new RowMapper<>() {
            @Override
            public PaymentMethodResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
                PaymentMethodResponse r = new PaymentMethodResponse();
                r.setPaymentMethodId(rs.getInt("payment_method_id"));
                r.setName(rs.getString("payment_method_name"));
                r.setColor(rs.getString("color_hex"));
                r.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
                return r;
            }
        };
    }

    public boolean existsName(Integer userId, String name) {
        String sql = "select exists(select 1 from payment_methods where user_id=? and payment_method_name=?)";
        Boolean ex = jdbc.queryForObject(sql, Boolean.class, userId, name);
        return ex != null && ex;
    }

    public PaymentMethodResponse insert(Integer userId, String name, String color) {
        String sql = """
            insert into payment_methods(user_id, payment_method_name, color_hex)
            values (?, ?, ?)
            returning payment_method_id, payment_method_name, color_hex, created_at
        """;
        return jdbc.queryForObject(sql, mapper(), userId, name, normalizeColor(color));
    }

    public List<PaymentMethodResponse> findAllByUser(Integer userId) {
        String sql = """
            select payment_method_id, payment_method_name, color_hex, created_at
            from payment_methods
            where user_id=? order by payment_method_id
        """;
        return jdbc.query(sql, mapper(), userId);
    }

    public Optional<PaymentMethodResponse> findOne(Integer userId, Integer paymentMethodId) {
        String sql = """
            select payment_method_id, payment_method_name, color_hex, created_at
            from payment_methods
            where user_id=? and payment_method_id=?
        """;
        var list = jdbc.query(sql, mapper(), userId, paymentMethodId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public int delete(Integer userId, Integer paymentMethodId) {
        return jdbc.update("delete from payment_methods where user_id=? and payment_method_id=?",
                userId, paymentMethodId);
    }

    private String normalizeColor(String c) {
        if (c == null || c.isBlank()) return null;
        return c.startsWith("#") ? c : ("#" + c);
    }
}
