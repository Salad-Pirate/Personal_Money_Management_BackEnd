package salad.example.pmm_be.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import salad.example.pmm_be.request.WalletRequest;
import salad.example.pmm_be.response.WalletResponse;
import salad.example.pmm_be.service.WalletService;

import java.util.List;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService service;

    public WalletController(WalletService service) {
        this.service = service;
    }

    private Integer resolveUserId(String header) {
        if (header == null || header.isBlank())
            throw new IllegalArgumentException("X-User-Id header is required");
        return Integer.valueOf(header);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WalletResponse createWallet(
            @RequestHeader(name = "X-User-Id") String userHeader,
            @Valid @RequestBody WalletRequest req
    ) {
        Integer userId = resolveUserId(userHeader);
        return service.create(userId, req);
    }

    @GetMapping
    public List<WalletResponse> listWallets(
            @RequestHeader(name = "X-User-Id") String userHeader
    ) {
        Integer userId = resolveUserId(userHeader);
        return service.list(userId);
    }

    @GetMapping("/{walletId}")
    public WalletResponse getWallet(
            @RequestHeader(name = "X-User-Id") String userHeader,
            @PathVariable Integer walletId
    ) {
        Integer userId = resolveUserId(userHeader);
        return service.getOne(userId, walletId);
    }

    @DeleteMapping("/{walletId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWallet(
            @RequestHeader(name = "X-User-Id") String userHeader,
            @PathVariable Integer walletId
    ) {
        Integer userId = resolveUserId(userHeader);
        service.delete(userId, walletId);
    }
}
