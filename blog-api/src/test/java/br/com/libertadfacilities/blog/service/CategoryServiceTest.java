package br.com.libertadfacilities.blog.service;

import br.com.libertadfacilities.blog.exception.BusinessRuleException;
import br.com.libertadfacilities.blog.model.Category;
import br.com.libertadfacilities.blog.repositories.CategoryRepository;
import br.com.libertadfacilities.blog.services.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("Deve criar a categoria com sucesso se o nome não existir")
    void shouldCreateCategorySuccessfully(){

        Category newCategory = new Category();
        newCategory.setName("Tecnologia");

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Tecnologia");

        when(categoryRepository.existsByName("Tecnologia")).thenReturn(false);

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        Category result = categoryService.createCategory(newCategory);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Tecnologia", result.getName());

        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Deve lançar exceççoes ao tentar criar categorias com nome já existente")
    void shouldThrowExceptionWhenCategoryAlreadyExists(){
        Category duplicatedCategory = new Category();
        duplicatedCategory.setName("Tecnologia");

        when(categoryRepository.existsByName("Tecnologia")).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () ->
                categoryService.createCategory(duplicatedCategory)
        );

        assertEquals("Uma categoria com este nome já existe.", exception.getMessage());

        verify(categoryRepository, never()).save(any(Category.class));
    }
}
