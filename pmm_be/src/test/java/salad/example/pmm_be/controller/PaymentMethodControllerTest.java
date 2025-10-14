package salad.example.pmm_be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import salad.example.pmm_be.request.PaymentMethodRequest;
import salad.example.pmm_be.response.PaymentMethodResponse;
import salad.example.pmm_be.service.PaymentMethodService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = PaymentMethodController.class)
class PaymentMethodControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockBean PaymentMethodService service;

    @Test
    void post_create_ok() throws Exception {
        PaymentMethodResponse res = new PaymentMethodResponse();
        res.setPaymentMethodId(1);
        res.setName("Cash");
        res.setColor("#3366FF");

        PaymentMethodRequest req = new PaymentMethodRequest();
        req.setName("Cash");
        req.setColor("#3366FF");

        Mockito.when(service.create(eq(1), any(PaymentMethodRequest.class))).thenReturn(res);

        mvc.perform(post("/payment-methods")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentMethodId").value(1))
                .andExpect(jsonPath("$.name").value("Cash"));
    }

    @Test
    void post_missingHeader_400() throws Exception {
        PaymentMethodRequest req = new PaymentMethodRequest();
        req.setName("Cash");
        req.setColor("#3366FF");

        mvc.perform(post("/payment-methods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void post_invalidBody_400() throws Exception {
        String bad = "{}";
        mvc.perform(post("/payment-methods")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bad))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_list_ok() throws Exception {
        PaymentMethodResponse r = new PaymentMethodResponse();
        r.setPaymentMethodId(1);
        r.setName("Cash");
        r.setColor("#3366FF");

        Mockito.when(service.list(1)).thenReturn(List.of(r));

        mvc.perform(get("/payment-methods").header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Cash"));
    }

    @Test
    void getOne_ok() throws Exception {
        PaymentMethodResponse r = new PaymentMethodResponse();
        r.setPaymentMethodId(99);
        r.setName("Bank");
        r.setColor("#00AAFF");

        Mockito.when(service.getOne(1, 99)).thenReturn(r);

        mvc.perform(get("/payment-methods/99").header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bank"));
    }

    @Test
    void delete_ok() throws Exception {
        mvc.perform(delete("/payment-methods/1").header("X-User-Id", "1"))
                .andExpect(status().isNoContent());

        Mockito.verify(service).delete(1, 1);
    }
}
