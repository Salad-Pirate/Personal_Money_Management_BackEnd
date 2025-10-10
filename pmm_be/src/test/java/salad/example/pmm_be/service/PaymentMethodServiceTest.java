package salad.example.pmm_be.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import salad.example.pmm_be.repository.PaymentMethodRepository;
import salad.example.pmm_be.request.PaymentMethodRequest;
import salad.example.pmm_be.response.PaymentMethodResponse;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for PaymentMethodService (mock repository)
 * Cover branch: success, duplicate, not found
 */
@ExtendWith(MockitoExtension.class)
class PaymentMethodServiceTest {

    @Mock
    PaymentMethodRepository repo;

    @InjectMocks
    PaymentMethodService service;

    PaymentMethodRequest req;
    PaymentMethodResponse res;

    @BeforeEach
    void setup() {
        req = new PaymentMethodRequest();
        req.setName("Cash");
        req.setColor("#3366FF");

        res = new PaymentMethodResponse();
        res.setPaymentMethodId(1);
        res.setName("Cash");
        res.setColor("#3366FF");
    }

    @Test
    void create_success() {
        when(repo.existsName(1, "Cash")).thenReturn(false);
        when(repo.insert(1, "Cash", "#3366FF")).thenReturn(res);

        var out = service.create(1, req);

        assertThat(out.getPaymentMethodId()).isEqualTo(1);
        assertThat(out.getName()).isEqualTo("Cash");
        verify(repo).existsName(1, "Cash");
        verify(repo).insert(1, "Cash", "#3366FF");
    }

    @Test
    void create_duplicate_throws() {
        when(repo.existsName(1, "Cash")).thenReturn(true);

        assertThatThrownBy(() -> service.create(1, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(repo, never()).insert(anyInt(), anyString(), anyString());
    }

    @Test
    void list_ok() {
        when(repo.findAllByUser(1)).thenReturn(List.of(res));

        var list = service.list(1);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getName()).isEqualTo("Cash");
        verify(repo).findAllByUser(1);
    }

    @Test
    void getOne_found() {
        when(repo.findOne(1, 1)).thenReturn(Optional.of(res));

        var found = service.getOne(1, 1);

        assertThat(found.getName()).isEqualTo("Cash");
        verify(repo).findOne(1, 1);
    }

    @Test
    void getOne_notFound_throws() {
        when(repo.findOne(1, 99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getOne(1, 99))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void delete_success() {
        when(repo.delete(1, 1)).thenReturn(1);

        service.delete(1, 1);

        verify(repo).delete(1, 1);
    }

    @Test
    void delete_notFound_throws() {
        when(repo.delete(1, 99)).thenReturn(0);

        assertThatThrownBy(() -> service.delete(1, 99))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }
}
