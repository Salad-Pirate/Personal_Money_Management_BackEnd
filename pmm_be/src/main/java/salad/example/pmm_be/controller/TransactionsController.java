package salad.example.pmm_be.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/debug")
public class TransactionsController {
    private final JdbcTemplate jdbc;

    public TransactionsController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // GET http://localhost:8080/debug/transactions
    @GetMapping("/transactions")
    public List<Map<String, Object>> list() {
        return jdbc.queryForList("select * from transactions order by transaction_id");
    }
}
