package br.com.libertadfacilities.blog.mapper;

import br.com.libertadfacilities.blog.dto.response.CategoryResponse;
import br.com.libertadfacilities.blog.entity.Category;

public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug()
        );
    }
}
