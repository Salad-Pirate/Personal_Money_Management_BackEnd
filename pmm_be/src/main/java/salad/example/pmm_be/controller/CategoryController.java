package salad.example.pmm_be.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import salad.example.pmm_be.request.CategoryRequest;
import salad.example.pmm_be.response.CategoryResponse;
import salad.example.pmm_be.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) { this.service = service; }

    private Integer resolveUserId(String header) {
        if (header == null || header.isBlank())
            throw new IllegalArgumentException("X-User-Id header is required");
        return Integer.valueOf(header);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(
            @RequestHeader("X-User-Id") String userHeader,
            @Valid @RequestBody CategoryRequest req
    ) {
        return service.create(resolveUserId(userHeader), req);
    }

    @GetMapping
    public List<CategoryResponse> list(
            @RequestHeader("X-User-Id") String userHeader
    ) {
        return service.list(resolveUserId(userHeader));
    }

    @GetMapping("/{id}")
    public CategoryResponse getOne(
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

    @PutMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCategory(
            @RequestHeader(name = "X-User-Id") String userHeader,
            @PathVariable Integer categoryId,
            @Valid @RequestBody CategoryRequest req
    ) {
        Integer userId = Integer.valueOf(userHeader);
        service.update(userId, categoryId, req);
    }
}
