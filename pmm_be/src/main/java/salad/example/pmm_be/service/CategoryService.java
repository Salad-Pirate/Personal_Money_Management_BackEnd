package salad.example.pmm_be.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import salad.example.pmm_be.repository.CategoryRepository;
import salad.example.pmm_be.request.CategoryRequest;
import salad.example.pmm_be.response.CategoryResponse;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository repo;

    public CategoryService(CategoryRepository repo) { this.repo = repo; }

    @Transactional
    public CategoryResponse create(Integer userId, CategoryRequest req) {
        String name = req.getName().trim();
        if (repo.existsName(userId, name)) {
            throw new IllegalArgumentException("Category name already exists for this user");
        }
        return repo.insert(userId, name, req.getType(), req.getColor());
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> list(Integer userId) {
        return repo.findAllByUser(userId);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getOne(Integer userId, Integer categoryId) {
        return repo.findOne(userId, categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    @Transactional
    public void delete(Integer userId, Integer categoryId) {
        int rows = repo.delete(userId, categoryId);
        if (rows == 0) throw new IllegalArgumentException("Category not found");
    }
}
