package salad.example.pmm_be.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import salad.example.pmm_be.repository.WalletRepository;
import salad.example.pmm_be.request.WalletRequest;
import salad.example.pmm_be.response.WalletResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    WalletRepository repo;

    @InjectMocks
    WalletService service;

    WalletRequest req;
    WalletResponse res;

    @BeforeEach
    void setup() {
        req = new WalletRequest();
        req.setName("Main Wallet");
        req.setColor("#00FF00");
        req.setBalance(BigDecimal.valueOf(1000.0));
        req.setType("CASH"); // ให้ชัวร์ว่า service จะส่งต่อเป็น "CASH"

        res = new WalletResponse();
        res.setWalletId(1);
        res.setName("Main Wallet");
        res.setColor("#00FF00");
        res.setType("CASH");
        res.setBalance(BigDecimal.valueOf(1000.0)); // Response ใช้ BigDecimal
    }

    @Test
    void create_success() {
        when(repo.existsName(1, "Main Wallet")).thenReturn(false);
        when(repo.insert(1, "Main Wallet", "CASH",
                BigDecimal.valueOf(1000.0), "#00FF00"))
                .thenReturn(res);

        WalletResponse result = service.create(1, req);

        assertThat(result.getWalletId()).isEqualTo(1);
        assertThat(result.getBalance()).isEqualByComparingTo("1000.0");
        verify(repo).insert(1, "Main Wallet", "CASH",
                BigDecimal.valueOf(1000.0), "#00FF00");
    }

    @Test
    void create_duplicateName_throws() {
        when(repo.existsName(1, "Main Wallet")).thenReturn(true);

        assertThatThrownBy(() -> service.create(1, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        // ห้ามเรียก insert เลย
        verify(repo, never()).insert(
                anyInt(), anyString(), anyString(), any(BigDecimal.class), anyString()
        );
    }

    @Test
    void list_ok() {
        when(repo.findAllByUser(1)).thenReturn(List.of(res));

        List<WalletResponse> list = service.list(1);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getName()).isEqualTo("Main Wallet");
        assertThat(list.get(0).getBalance()).isEqualByComparingTo("1000.0");
        verify(repo).findAllByUser(1);
    }

    @Test
    void getOne_found() {
        when(repo.findOne(1, 1)).thenReturn(Optional.of(res));

        WalletResponse found = service.getOne(1, 1);

        assertThat(found.getName()).isEqualTo("Main Wallet");
        assertThat(found.getBalance()).isEqualByComparingTo("1000.0");
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
