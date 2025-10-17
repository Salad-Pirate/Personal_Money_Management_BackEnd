package salad.example.pmm_be.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import salad.example.pmm_be.response.CategoryResponse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepository {

    private final JdbcTemplate jdbc;

    public CategoryRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private RowMapper<CategoryResponse> mapper() {
        return new RowMapper<>() {
            @Override
            public CategoryResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
                CategoryResponse r = new CategoryResponse();
                r.setCategoryId(rs.getInt("category_id"));
                r.setName(rs.getString("category_name"));
                r.setType(rs.getString("category_type"));
                r.setColor(rs.getString("color_hex"));
                r.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
                return r;
            }
        };
    }

    public boolean existsName(Integer userId, String name) {
        String sql = "select exists(select 1 from categories where user_id=? and category_name=?)";
        Boolean ex = jdbc.queryForObject(sql, Boolean.class, userId, name);
        return ex != null && ex;
    }

    public CategoryResponse insert(Integer userId, String name, String type, String color) {
        String sql = """
            insert into categories(user_id, category_name, category_type, color_hex)
            values (?, ?, ?, ?)
            returning category_id, category_name, category_type, color_hex, created_at
        """;
        return jdbc.queryForObject(sql, mapper(), userId, name, type, normalizeColor(color));
    }

    public List<CategoryResponse> findAllByUser(Integer userId) {
        String sql = """
            select category_id, category_name, category_type, color_hex, created_at
            from categories
            where user_id=? order by category_id
        """;
        return jdbc.query(sql, mapper(), userId);
    }

    public Optional<CategoryResponse> findOne(Integer userId, Integer categoryId) {
        String sql = """
            select category_id, category_name, category_type, color_hex, created_at
            from categories
            where user_id=? and category_id=?
        """;
        var list = jdbc.query(sql, mapper(), userId, categoryId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public int delete(Integer userId, Integer categoryId) {
        return jdbc.update("delete from categories where user_id=? and category_id=?",
                userId, categoryId);
    }

    private String normalizeColor(String c) {
        if (c == null || c.isBlank()) return null;
        return c.startsWith("#") ? c : ("#" + c);
    }

    public int update(Integer userId, Integer categoryId, String name, String type, String color) {
        String sql = """
        update categories
        set category_name = ?, category_type = ?, color_hex = ?
        where user_id = ? and category_id = ?
    """;
        return jdbc.update(sql,
                name,
                type,
                (color == null || color.isBlank()) ? null : (color.startsWith("#") ? color : "#" + color),
                userId,
                categoryId);
    }
}
