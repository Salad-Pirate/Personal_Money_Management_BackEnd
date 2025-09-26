package salad.example.pmm_be.repository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import salad.example.pmm_be.response.WalletResponse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class WalletRepository {

    private final JdbcTemplate jdbc;

    public WalletRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private RowMapper<WalletResponse> mapper() {
        return new RowMapper<>() {
            @Override
            public WalletResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
                WalletResponse r = new WalletResponse();
                r.setWalletId(rs.getInt("wallet_id"));
                r.setName(rs.getString("wallet_name"));
                r.setBalance(rs.getBigDecimal("starting_balance"));
                r.setType(rs.getString("wallet_type"));
                r.setColor(rs.getString("color_hex"));
                OffsetDateTime odt = rs.getObject("created_at", OffsetDateTime.class);
                r.setCreatedAt(odt);
                return r;
            }
        };
    }

    public boolean existsName(Integer userId, String walletName) {
        String sql = "select exists(select 1 from wallets where user_id=? and wallet_name=?)";
        Boolean exists = jdbc.queryForObject(sql, Boolean.class, userId, walletName);
        return exists != null && exists;
    }

    public WalletResponse insert(Integer userId, String name, String type, java.math.BigDecimal balance, String color) {
        String sql = """
            insert into wallets(user_id, wallet_name, starting_balance, wallet_type, color_hex)
            values (?, ?, ?, ?, ?)
            returning wallet_id, wallet_name, starting_balance, wallet_type, color_hex, created_at
            """;
        return jdbc.queryForObject(sql, mapper(), userId, name, balance, type, normalizeColor(color));
    }

    public List<WalletResponse> findAllByUser(Integer userId) {
        String sql = """
            select wallet_id, wallet_name, starting_balance, wallet_type, color_hex, created_at
            from wallets
            where user_id=?
            order by wallet_id
            """;
        return jdbc.query(sql, mapper(), userId);
    }

    public Optional<WalletResponse> findOne(Integer userId, Integer walletId) {
        String sql = """
            select wallet_id, wallet_name, starting_balance, wallet_type, color_hex, created_at
            from wallets
            where user_id=? and wallet_id=?
            """;
        List<WalletResponse> list = jdbc.query(sql, mapper(), userId, walletId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public int delete(Integer userId, Integer walletId) throws DataIntegrityViolationException {
        String sql = "delete from wallets where user_id=? and wallet_id=?";
        return jdbc.update(sql, userId, walletId);
    }

    private String normalizeColor(String c) {
        if (c == null || c.isBlank()) return null;
        return c.startsWith("#") ? c : ("#" + c);
    }
}
