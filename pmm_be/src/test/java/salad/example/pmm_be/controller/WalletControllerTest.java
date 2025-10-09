package salad.example.pmm_be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import salad.example.pmm_be.request.WalletRequest;
import salad.example.pmm_be.response.WalletResponse;
import salad.example.pmm_be.service.WalletService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = WalletController.class)
class WalletControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockBean WalletService service;

    @Test
    void post_create_ok() throws Exception {
        // response (service -> controller)
        WalletResponse res = new WalletResponse();
        res.setWalletId(1);
        res.setName("Main Wallet");
        res.setType("CASH");
        res.setColor("#00FF00");
        res.setBalance(BigDecimal.valueOf(1000.0));

        // request (client -> controller)
        WalletRequest req = new WalletRequest();
        req.setName("Main Wallet");
        req.setType("CASH"); // << ต้องมี
        req.setColor("#00FF00");
        req.setBalance(BigDecimal.valueOf(1000.0));

        Mockito.when(service.create(eq(1), any(WalletRequest.class))).thenReturn(res);

        mvc.perform(post("/wallets")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.walletId").value(1))
                .andExpect(jsonPath("$.name").value("Main Wallet"))
                .andExpect(jsonPath("$.type").value("CASH"))
                .andExpect(jsonPath("$.color").value("#00FF00"))
                .andExpect(jsonPath("$.balance").value(1000.0));
    }

    @Test
    void post_missingHeader_shouldReturn400() throws Exception {
        WalletRequest req = new WalletRequest();
        req.setName("Main Wallet");
        req.setType("CASH"); // << กัน validation ล้ม
        req.setColor("#00FF00");
        req.setBalance(BigDecimal.valueOf(1000.0));

        mvc.perform(post("/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_list_ok() throws Exception {
        WalletResponse w = new WalletResponse();
        w.setWalletId(1);
        w.setName("Main Wallet");
        w.setType("CASH");
        w.setColor("#00FF00");
        w.setBalance(BigDecimal.valueOf(1000.0));

        Mockito.when(service.list(1)).thenReturn(List.of(w));

        mvc.perform(get("/wallets")
                        .header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Main Wallet"))
                .andExpect(jsonPath("$[0].type").value("CASH"))
                .andExpect(jsonPath("$[0].color").value("#00FF00"))
                .andExpect(jsonPath("$[0].balance").value(1000.0));
    }

    @Test
    void getOne_ok() throws Exception {
        WalletResponse res = new WalletResponse();
        res.setWalletId(99);
        res.setName("Travel");
        res.setType("CASH");
        res.setColor("#FF00FF");
        res.setBalance(BigDecimal.valueOf(500.0));

        Mockito.when(service.getOne(1, 99)).thenReturn(res);

        mvc.perform(get("/wallets/99").header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Travel"))
                .andExpect(jsonPath("$.type").value("CASH"))
                .andExpect(jsonPath("$.color").value("#FF00FF"))
                .andExpect(jsonPath("$.balance").value(500.0));
    }

    @Test
    void delete_ok() throws Exception {
        mvc.perform(delete("/wallets/1").header("X-User-Id", "1"))
                .andExpect(status().isNoContent());
        Mockito.verify(service).delete(1, 1);
    }
}
