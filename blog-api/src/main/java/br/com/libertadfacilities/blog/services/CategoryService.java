package br.com.libertadfacilities.blog.services;

import br.com.libertadfacilities.blog.exception.BusinessRuleException;
import br.com.libertadfacilities.blog.model.Category;
import br.com.libertadfacilities.blog.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;


    public Category createCategory(Category category) {

        if (categoryRepository.existsByName(category.getName())) {
            throw new BusinessRuleException("Uma categoria com este nome já existe.");
        }

        return categoryRepository.save(category);
    }


    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}