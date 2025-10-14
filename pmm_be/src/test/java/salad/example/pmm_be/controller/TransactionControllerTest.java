package salad.example.pmm_be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import salad.example.pmm_be.request.TransactionRequest;
import salad.example.pmm_be.response.TransactionItemResponse;
import salad.example.pmm_be.service.TransactionService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TransactionController.class)
class TransactionControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockBean TransactionService service;

    @Test
    void post_create_ok() throws Exception {
        //request body follow by Transaction Request
        TransactionRequest req = new TransactionRequest();
        req.setCategoryId(7);
        req.setPaymentMethodId(2);
        req.setWalletId(1);
        req.setAmount(new BigDecimal("123.45"));
        req.setType("Expense");
        req.setOccuredAt(OffsetDateTime.parse("2025-09-15T12:00:00+07:00"));
        req.setTransactionLocation("BKK");
        req.setNote("coffee");

        // service response
        TransactionItemResponse r = new TransactionItemResponse();
        r.setTransactionId(100);
        r.setTransactionType("Expense");
        r.setAmount(new BigDecimal("123.45"));

        Mockito.when(service.create(eq(1), any(TransactionRequest.class))).thenReturn(r);

        mvc.perform(post("/transactions")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value(100))
                .andExpect(jsonPath("$.transactionType").value("Expense"))
                .andExpect(jsonPath("$.amount").value(123.45));
    }

    @Test
    void post_invalidBody_400() throws Exception {
        mvc.perform(post("/transactions")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_list_noFilters_ok() throws Exception {
        TransactionItemResponse t = new TransactionItemResponse();
        t.setTransactionId(1);
        t.setTransactionType("Income");
        t.setAmount(new BigDecimal("10.00"));

        Mockito.when(service.list(eq(1), isNull(), isNull(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(List.of(t));

        mvc.perform(get("/transactions")
                        .header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value(1))
                .andExpect(jsonPath("$[0].transactionType").value("Income"))
                .andExpect(jsonPath("$[0].amount").value(10.00));
    }

    @Test
    void get_list_withAllFilters_ok_andVerifyParsing() throws Exception {
        TransactionItemResponse t = new TransactionItemResponse();
        t.setTransactionId(2);
        t.setTransactionType("Expense");

        Mockito.when(service.list(anyInt(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(t));

        mvc.perform(get("/transactions")
                        .header("X-User-Id", "1")
                        .param("type", "Expense")
                        .param("categoryId", "7")
                        .param("from", "2025-09-01T00:00:00+07:00")
                        .param("to",   "2025-09-30T23:59:59+07:00")
                        .param("limit","20")
                        .param("offset","0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value(2));

        ArgumentCaptor<OffsetDateTime> fromCap = ArgumentCaptor.forClass(OffsetDateTime.class);
        ArgumentCaptor<OffsetDateTime> toCap = ArgumentCaptor.forClass(OffsetDateTime.class);
        Mockito.verify(service).list(eq(1), eq("Expense"), eq(7), fromCap.capture(), toCap.capture(), eq(20), eq(0));
        assertThat(fromCap.getValue()).isEqualTo(OffsetDateTime.parse("2025-09-01T00:00:00+07:00"));
        assertThat(toCap.getValue()).isEqualTo(OffsetDateTime.parse("2025-09-30T23:59:59+07:00"));
    }

    @Test
    void get_list_missingHeader_400() throws Exception {
        mvc.perform(get("/transactions"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_ok() throws Exception {
        mvc.perform(delete("/transactions/200")
                        .header("X-User-Id", "1"))
                .andExpect(status().isNoContent());

        Mockito.verify(service).delete(1, 200);
    }
}
