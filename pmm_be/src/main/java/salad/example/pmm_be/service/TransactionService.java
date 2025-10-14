package salad.example.pmm_be.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import salad.example.pmm_be.repository.TransactionRepository;
import salad.example.pmm_be.request.TransactionRequest;
import salad.example.pmm_be.response.TransactionItemResponse;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository repo;

    public TransactionService(TransactionRepository repo) { this.repo = repo; }

    @Transactional
    public TransactionItemResponse create(Integer userId, TransactionRequest req) {
        return repo.insert(userId, req);
    }

    @Transactional(readOnly = true)
    public List<TransactionItemResponse> list(
            Integer userId,
            String type,
            Integer categoryId,
            OffsetDateTime from,
            OffsetDateTime to,
            Integer limit,
            Integer offset
    ) {
        return repo.list(userId, type, categoryId, from, to, limit, offset);
    }

    @Transactional
    public void delete(Integer userId, Integer txId) {
        int rows = repo.delete(userId, txId);
        if (rows == 0) throw new IllegalArgumentException("Transaction not found");
    }
}
