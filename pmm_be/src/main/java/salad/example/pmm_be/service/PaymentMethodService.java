package salad.example.pmm_be.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import salad.example.pmm_be.repository.PaymentMethodRepository;
import salad.example.pmm_be.request.PaymentMethodRequest;
import salad.example.pmm_be.response.PaymentMethodResponse;

import java.util.List;

@Service
public class PaymentMethodService {

    private final PaymentMethodRepository repo;

    public PaymentMethodService(PaymentMethodRepository repo) { this.repo = repo; }

    @Transactional
    public PaymentMethodResponse create(Integer userId, PaymentMethodRequest req) {
        String name = req.getName().trim();
        if (repo.existsName(userId, name)) {
            throw new IllegalArgumentException("Payment method name already exists for this user");
        }
        return repo.insert(userId, name, req.getColor());
    }

    @Transactional(readOnly = true)
    public List<PaymentMethodResponse> list(Integer userId) {
        return repo.findAllByUser(userId);
    }

    @Transactional(readOnly = true)
    public PaymentMethodResponse getOne(Integer userId, Integer paymentMethodId) {
        return repo.findOne(userId, paymentMethodId)
                .orElseThrow(() -> new IllegalArgumentException("Payment method not found"));
    }

    @Transactional
    public void delete(Integer userId, Integer paymentMethodId) {
        int rows = repo.delete(userId, paymentMethodId);
        if (rows == 0) throw new IllegalArgumentException("Payment method not found");
    }

    @Transactional
    public void update(Integer userId, Integer paymentMethodId, PaymentMethodRequest req) {
        // ตรวจชื่อซ้ำใน user เดียวกัน
        if (repo.existsName(userId, req.getName().trim())) {
            var current = repo.findOne(userId, paymentMethodId);
            if (current.isEmpty() || !current.get().getName().equalsIgnoreCase(req.getName().trim())) {
                throw new IllegalArgumentException("Payment method name already exists for this user");
            }
        }

        int rows = repo.update(
                userId,
                paymentMethodId,
                req.getName().trim(),
                req.getColor()
        );

        if (rows == 0) {
            throw new IllegalArgumentException("Payment method not found or not belong to this user");
        }
    }
}
