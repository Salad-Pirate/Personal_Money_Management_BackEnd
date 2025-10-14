package salad.example.pmm_be.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import salad.example.pmm_be.repository.CategoryRepository;
import salad.example.pmm_be.request.CategoryRequest;
import salad.example.pmm_be.response.CategoryResponse;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit Test (AAA / Given-When-Then)
 * ครอบคลุมทุก branch ของ CategoryService
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    CategoryRepository repo;

    @InjectMocks
    CategoryService service;

    CategoryRequest req;
    CategoryResponse res;

    @BeforeEach
    void setUp() {
        req = new CategoryRequest();
        req.setName("Food");
        req.setType("Expense");
        req.setColor("#FF0000");

        res = new CategoryResponse();
        res.setCategoryId(1);
        res.setName("Food");
        res.setType("Expense");
        res.setColor("#FF0000");
    }

    @Test
    void create_success() {
        // Arrange
        when(repo.existsName(1, "Food")).thenReturn(false);
        when(repo.insert(1, "Food", "Expense", "#FF0000")).thenReturn(res);

        // Act
        CategoryResponse out = service.create(1, req);

        // Assert
        assertThat(out.getCategoryId()).isEqualTo(1);
        verify(repo).existsName(1, "Food");
        verify(repo).insert(1, "Food", "Expense", "#FF0000");
    }

    @Test
    void create_duplicateName_throws() {
        when(repo.existsName(1, "Food")).thenReturn(true);
        assertThatThrownBy(() -> service.create(1, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
        verify(repo, never()).insert(any(), any(), any(), any());
    }

    @Test
    void list_ok() {
        when(repo.findAllByUser(1)).thenReturn(List.of(res));
        var list = service.list(1);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getName()).isEqualTo("Food");
        verify(repo).findAllByUser(1);
    }

    @Test
    void getOne_success() {
        when(repo.findOne(1, 1)).thenReturn(Optional.of(res));
        var found = service.getOne(1, 1);
        assertThat(found.getName()).isEqualTo("Food");
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
