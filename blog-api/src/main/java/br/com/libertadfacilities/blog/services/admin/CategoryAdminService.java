package br.com.libertadfacilities.blog.services.admin;


import br.com.libertadfacilities.blog.dto.request.CreateCategoryRequest;
import br.com.libertadfacilities.blog.dto.request.UpdateCategoryRequest;
import br.com.libertadfacilities.blog.dto.response.CategoryResponse;
import br.com.libertadfacilities.blog.entity.Category;
import br.com.libertadfacilities.blog.exception.BusinessRuleException;
import br.com.libertadfacilities.blog.exception.ResourceNotFoundException;
import br.com.libertadfacilities.blog.mapper.CategoryMapper;
import br.com.libertadfacilities.blog.repositories.CategoryRepository;
import br.com.libertadfacilities.blog.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryAdminService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryResponse create(CreateCategoryRequest request) {
        String slug = SlugUtil.toSlug(request.name());

        if(categoryRepository.existsByName(request.name())) {
            throw new BusinessRuleException("Já existe uma categoria com esse nome");
        }

        if(categoryRepository.existsBySlug(slug)) {
            throw new BusinessRuleException("Já existe categoria com esse slug");
        }

        Category category = new Category();
        category.setName(request.name().trim());
        category.setSlug(slug);

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    public List<CategoryResponse> findAll(){
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    public CategoryResponse update (Long id, UpdateCategoryRequest request){
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Categoria não encontrada"));

        String slug = SlugUtil.toSlug(request.name());

        if(!category.getName().equalsIgnoreCase(request.name().trim())
        && categoryRepository.existsByName(request.name().trim())) {
            throw new BusinessRuleException("Já existe uma categoria com esse nome");
        }

        if(!category.getSlug().equalsIgnoreCase(slug)
        && categoryRepository.existsBySlug(slug)) {
            throw new BusinessRuleException("Já existe uma categoria com esse slug");
        }

        category.setName(request.name().trim());
        category.setSlug(slug);

        Category updated =categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    public void delete(Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Categoria não encontrada"));
    }
}
