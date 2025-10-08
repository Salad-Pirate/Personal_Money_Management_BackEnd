package salad.example.pmm_be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import salad.example.pmm_be.request.CategoryRequest;
import salad.example.pmm_be.response.CategoryResponse;
import salad.example.pmm_be.service.CategoryService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;




/**
 * Integration Test (Controller + MockMvc)
 * ทดสอบ HTTP layer ตามแนวทางในสไลด์
 */
@WebMvcTest(controllers = CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(salad.example.pmm_be.infra.GlobalExceptionHandler.class)

class CategoryControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockBean CategoryService service;

    @Test
    void post_create_ok() throws Exception {
        CategoryResponse res = new CategoryResponse();
        res.setCategoryId(1);
        res.setName("Food");
        res.setType("Expense");
        res.setColor("#FF0000");

        Mockito.when(service.create(eq(1), any(CategoryRequest.class))).thenReturn(res);

        CategoryRequest req = new CategoryRequest();
        req.setName("Food");
        req.setType("Expense");
        req.setColor("#FF0000");

        mvc.perform(post("/categories")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.name").value("Food"));
    }

    @Test
    void post_missingHeader_shouldReturn400() throws Exception {
        CategoryRequest req = new CategoryRequest();
        req.setName("Food");
        req.setType("Expense");
        req.setColor("#FF0000");

        mvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_list_ok() throws Exception {
        CategoryResponse r1 = new CategoryResponse();
        r1.setCategoryId(1);
        r1.setName("Food");
        r1.setType("Expense");
        r1.setColor("#FF0000");

        Mockito.when(service.list(1)).thenReturn(List.of(r1));

        mvc.perform(get("/categories").header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Food"));
    }

    @Test
    void getOne_ok() throws Exception {
        CategoryResponse res = new CategoryResponse();
        res.setCategoryId(99);
        res.setName("Transport");
        res.setType("Expense");
        res.setColor("#00FF00");

        Mockito.when(service.getOne(1, 99)).thenReturn(res);

        mvc.perform(get("/categories/99").header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Transport"));
    }

    @Test
    void delete_ok() throws Exception {
        mvc.perform(delete("/categories/1").header("X-User-Id", "1"))
                .andExpect(status().isNoContent());
        Mockito.verify(service).delete(1, 1);
    }
}
