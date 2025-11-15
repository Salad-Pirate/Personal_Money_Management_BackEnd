package salad.example.pmm_be.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import salad.example.pmm_be.response.TransactionItemResponse;
import salad.example.pmm_be.request.TransactionRequest;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public class TransactionRepository {

    private final JdbcTemplate jdbc;

    public TransactionRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static BigDecimal signed(BigDecimal amt, String type) {
        return "Expense".equals(type) ? amt.negate() : amt;
    }

    private RowMapper<TransactionItemResponse> mapper() {
        return new RowMapper<>() {
            @Override
            public TransactionItemResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
                TransactionItemResponse r = new TransactionItemResponse();
                r.setTransactionId(rs.getInt("transaction_id"));

                r.setCategoryId(rs.getInt("category_id"));
                r.setCategoryName(rs.getString("category_name"));
                r.setCategoryType(rs.getString("category_type"));

                r.setPaymentMethodId(rs.getInt("payment_method_id"));
                r.setPaymentMethodName(rs.getString("payment_method_name"));

                r.setWalletId(rs.getInt("wallet_id"));
                r.setWalletName(rs.getString("wallet_name"));

                var amt = rs.getBigDecimal("amount");
                var type = rs.getString("transaction_type");
                r.setAmount(amt);
                r.setTransactionType(type);
                r.setSignedAmount(signed(amt, type));

                r.setOccuredAt(rs.getObject("occured_at", OffsetDateTime.class));
                r.setTransactionLocation(rs.getString("transaction_location"));
                r.setNote(rs.getString("note"));
                r.setLatitude(rs.getObject("latitude", Double.class));
                r.setLongitude(rs.getObject("longitude", Double.class));
                return r;
            }
        };
    }

    /** Insert แล้วคืนแถวที่ join พร้อมใช้งาน */
    public TransactionItemResponse insert(Integer userId, TransactionRequest req) {
        String insert = """
            insert into transactions
            (user_id, category_id, payment_method_id, wallet_id,
             amount, transaction_type, occured_at, transaction_location, note, latitude, longitude)
            values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            returning transaction_id
        """;
        Integer newId = jdbc.queryForObject(
                insert, Integer.class,
                userId,
                req.getCategoryId(),
                req.getPaymentMethodId(),
                req.getWalletId(),
                req.getAmount(),
                req.getType(),
                req.getOccuredAt(),
                req.getTransactionLocation(),
                req.getNote(),
                req.getLatitude(),
                req.getLongitude()
        );
        return findOne(userId, newId);
    }

    public TransactionItemResponse findOne(Integer userId, Integer txId) {
        String sql = BASE_SELECT + " where t.user_id=? and t.transaction_id=? order by t.occured_at desc";
        return jdbc.queryForObject(sql, mapper(), userId, txId);
    }

    /** list พร้อม filter optional: type, categoryId, dateFrom, dateTo, limit/offset */
    public List<TransactionItemResponse> list(
            Integer userId,
            String type,            // null = all
            Integer categoryId,     // null = all
            OffsetDateTime from,    // null = no lower bound
            OffsetDateTime to,      // null = no upper bound
            Integer limit,          // null = default 100
            Integer offset          // null = 0
    ) {
        StringBuilder sb = new StringBuilder(BASE_SELECT)
                .append(" where t.user_id=?");
        new Object() { }; // keep spot

        new Object();

        new Object();
        new Object();

        // dynamic conditions
        new Object();
        var params = new java.util.ArrayList<>();
        params.add(userId);

        if (type != null && !type.isBlank() && (type.equals("Income") || type.equals("Expense"))) {
            sb.append(" and t.transaction_type = ?");
            params.add(type);
        }
        if (categoryId != null) {
            sb.append(" and t.category_id = ?");
            params.add(categoryId);
        }
        if (from != null) {
            sb.append(" and t.occured_at >= ?");
            params.add(from);
        }
        if (to != null) {
            sb.append(" and t.occured_at < ?");
            params.add(to);
        }

        sb.append(" order by t.occured_at desc, t.transaction_id desc");

        if (limit == null || limit <= 0) limit = 100;
        if (offset == null || offset < 0) offset = 0;
        sb.append(" limit ").append(limit).append(" offset ").append(offset);

        return jdbc.query(sb.toString(), mapper(), params.toArray());
    }

    public int delete(Integer userId, Integer txId) {
        return jdbc.update("delete from transactions where user_id=? and transaction_id=?", userId, txId);
    }

    private static final String BASE_SELECT = """
        select
          t.transaction_id,
          t.category_id,
          c.category_name,
          c.category_type,
          t.payment_method_id,
          pm.payment_method_name,
          t.wallet_id,
          w.wallet_name,
          t.amount,
          t.transaction_type,
          t.occured_at,
          t.transaction_location,
          t.note,
          t.latitude,
          t.longitude
        from transactions t
        join categories c
          on c.user_id = t.user_id and c.category_id = t.category_id
        join payment_methods pm
          on pm.user_id = t.user_id and pm.payment_method_id = t.payment_method_id
        join wallets w
          on w.user_id = t.user_id and w.wallet_id = t.wallet_id
        """;
}
