package salad.example.pmm_be.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import salad.example.pmm_be.request.TransactionRequest;
import salad.example.pmm_be.response.TransactionItemResponse;
import salad.example.pmm_be.service.TransactionService;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) { this.service = service; }

    private Integer resolveUserId(String header) {
        if (header == null || header.isBlank())
            throw new IllegalArgumentException("X-User-Id header is required");
        return Integer.valueOf(header);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionItemResponse create(
            @RequestHeader("X-User-Id") String userHeader,
            @Valid @RequestBody TransactionRequest req
    ) {
        return service.create(resolveUserId(userHeader), req);
    }

    /** list + filters: ?type=Income|Expense&categoryId=..&from=2025-09-01T00:00:00Z&to=...&limit=50&offset=0 */
    @GetMapping
    public List<TransactionItemResponse> list(
            @RequestHeader("X-User-Id") String userHeader,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) OffsetDateTime from,
            @RequestParam(required = false) OffsetDateTime to,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset
    ) {
        return service.list(resolveUserId(userHeader), type, categoryId, from, to, limit, offset);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestHeader("X-User-Id") String userHeader,
            @PathVariable("id") Integer id
    ) {
        service.delete(resolveUserId(userHeader), id);
    }
}
