package br.com.libertadfacilities.blog.controller.admin;

import br.com.libertadfacilities.blog.dto.request.CreateCategoryRequest;
import br.com.libertadfacilities.blog.dto.request.UpdateCategoryRequest;
import br.com.libertadfacilities.blog.dto.response.CategoryResponse;
import br.com.libertadfacilities.blog.dto.response.PageResponse;
import br.com.libertadfacilities.blog.services.admin.CategoryAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {

    private final CategoryAdminService categoryAdminService;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@RequestBody @Valid CreateCategoryRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryAdminService.create(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<CategoryResponse>> findAll(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.status(HttpStatus.OK).body(categoryAdminService.findAll(query, page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id, @RequestBody @Valid UpdateCategoryRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(categoryAdminService.update(id,request));
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<CategoryResponse> delete(@PathVariable Long id){
        categoryAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
