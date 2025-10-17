package salad.example.pmm_be.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import salad.example.pmm_be.repository.WalletRepository;
import salad.example.pmm_be.request.WalletRequest;
import salad.example.pmm_be.response.WalletResponse;

import java.util.List;

@Service
public class WalletService {

    private final WalletRepository repo;

    public WalletService(WalletRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public WalletResponse create(Integer userId, WalletRequest req) {
        if (repo.existsName(userId, req.getName().trim())) {
            throw new IllegalArgumentException("Wallet name already exists for this user");
        }
        return repo.insert(
                userId,
                req.getName().trim(),
                req.getType().trim(),
                req.getBalance(),
                req.getColor()
        );
    }

    @Transactional(readOnly = true)
    public List<WalletResponse> list(Integer userId) {
        return repo.findAllByUser(userId);
    }

    @Transactional(readOnly = true)
    public WalletResponse getOne(Integer userId, Integer walletId) {
        return repo.findOne(userId, walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
    }

    @Transactional
    public void delete(Integer userId, Integer walletId) {
        int rows = repo.delete(userId, walletId);
        if (rows == 0) throw new IllegalArgumentException("Wallet not found");
    }

    @Transactional
    public void update(Integer userId, Integer walletId, WalletRequest req) {

        int rows = repo.update(
                userId, walletId,
                req.getName().trim(),
                req.getType().trim(),
                req.getBalance(),
                req.getColor()
        );

        if (rows == 0) {
            throw new IllegalArgumentException("Wallet not found or not belong to this user");
        }
    }
}
