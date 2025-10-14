package salad.example.pmm_be.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import salad.example.pmm_be.repository.TransactionRepository;
import salad.example.pmm_be.request.TransactionRequest;
import salad.example.pmm_be.response.TransactionItemResponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock TransactionRepository repo;
    @InjectMocks TransactionService service;

    TransactionRequest req;
    TransactionItemResponse resp;
    OffsetDateTime from, to;

    @BeforeEach
    void setup() {
        req = new TransactionRequest();
        req.setCategoryId(7);
        req.setPaymentMethodId(2);
        req.setWalletId(1);
        req.setAmount(new BigDecimal("123.45"));
        req.setType("Expense"); // ต้องใช้ 'type'
        req.setOccuredAt(OffsetDateTime.parse("2025-09-15T12:00:00+07:00"));
        req.setTransactionLocation("BKK");
        req.setNote("coffee");

        resp = new TransactionItemResponse();
        resp.setTransactionId(100);
        resp.setCategoryId(7);
        resp.setPaymentMethodId(2);
        resp.setWalletId(1);
        resp.setAmount(new BigDecimal("123.45"));
        resp.setTransactionType("Expense");
        resp.setOccuredAt(OffsetDateTime.parse("2025-09-15T12:00:00+07:00"));

        from = OffsetDateTime.parse("2025-09-01T00:00:00+07:00");
        to   = OffsetDateTime.parse("2025-09-30T23:59:59+07:00");
    }

    @Test
    void create_ok() {
        when(repo.insert(1, req)).thenReturn(resp);

        var out = service.create(1, req);

        assertThat(out.getTransactionId()).isEqualTo(100);
        verify(repo).insert(1, req);
    }

    @Test
    void list_noFilters_ok() {
        when(repo.list(1, null, null, null, null, null, null))
                .thenReturn(List.of(resp));

        var out = service.list(1, null, null, null, null, null, null);

        assertThat(out).hasSize(1);
        verify(repo).list(1, null, null, null, null, null, null);
    }

    @Test
    void list_allFilters_ok() {
        when(repo.list(1, "Expense", 7, from, to, 20, 0))
                .thenReturn(List.of(resp));

        var out = service.list(1, "Expense", 7, from, to, 20, 0);

        assertThat(out).hasSize(1);
        verify(repo).list(1, "Expense", 7, from, to, 20, 0);
    }

    @Test
    void delete_ok() {
        when(repo.delete(1, 100)).thenReturn(1);

        service.delete(1, 100);

        verify(repo).delete(1, 100);
    }

    @Test
    void delete_notFound_throws() {
        when(repo.delete(1, 404)).thenReturn(0);

        assertThatThrownBy(() -> service.delete(1, 404))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }
}
