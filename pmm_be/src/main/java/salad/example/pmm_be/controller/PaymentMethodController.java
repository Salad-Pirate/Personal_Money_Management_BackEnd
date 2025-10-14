package salad.example.pmm_be.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import salad.example.pmm_be.request.PaymentMethodRequest;
import salad.example.pmm_be.response.PaymentMethodResponse;
import salad.example.pmm_be.service.PaymentMethodService;

import java.util.List;

@RestController
@RequestMapping("/payment-methods")
public class PaymentMethodController {

    private final PaymentMethodService service;

    public PaymentMethodController(PaymentMethodService service) { this.service = service; }

    private Integer resolveUserId(String header) {
        if (header == null || header.isBlank())
            throw new IllegalArgumentException("X-User-Id header is required");
        return Integer.valueOf(header);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentMethodResponse create(
            @RequestHeader("X-User-Id") String userHeader,
            @Valid @RequestBody PaymentMethodRequest req
    ) {
        return service.create(resolveUserId(userHeader), req);
    }

    @GetMapping
    public List<PaymentMethodResponse> list(
            @RequestHeader("X-User-Id") String userHeader
    ) {
        return service.list(resolveUserId(userHeader));
    }

    @GetMapping("/{id}")
    public PaymentMethodResponse getOne(
            @RequestHeader("X-User-Id") String userHeader,
            @PathVariable("id") Integer id
    ) {
        return service.getOne(resolveUserId(userHeader), id);
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
