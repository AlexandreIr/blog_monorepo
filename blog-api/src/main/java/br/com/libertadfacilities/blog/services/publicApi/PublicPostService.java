package br.com.libertadfacilities.blog.services.publicApi;


import br.com.libertadfacilities.blog.dto.response.PageResponse;
import br.com.libertadfacilities.blog.dto.response.PublicPostDetailResponse;
import br.com.libertadfacilities.blog.dto.response.PublicPostSummaryResponse;
import br.com.libertadfacilities.blog.entity.Post;
import br.com.libertadfacilities.blog.enums.PostStatus;
import br.com.libertadfacilities.blog.exception.ResourceNotFoundException;
import br.com.libertadfacilities.blog.mapper.PostMapper;
import br.com.libertadfacilities.blog.repositories.CategoryRepository;
import br.com.libertadfacilities.blog.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicPostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final PostMapper mapper;

    public PageResponse<PublicPostSummaryResponse> listPublished(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findAllByStatus(PostStatus.PUBLISHED, pageable);

        return toPagedSummaryResponse(posts);
    }

    public PublicPostDetailResponse findBySlug(String slug){
        Post post = postRepository.findBySlugAndStatus(slug, PostStatus.PUBLISHED)
                .orElseThrow(()-> new ResourceNotFoundException("Post publicado não encontrado"));

        return mapper.toDetailResponse(post);
    }

    public PageResponse<PublicPostSummaryResponse> findByCategory(String categorySlug, int page, int size) {
        categoryRepository.findBySlug(categorySlug)
                .orElseThrow(()-> new ResourceNotFoundException("Categoria não encontrada"));

        Pageable pageable = buildPageable(page, size);
        Page<Post> posts = postRepository.findPublishedByCategorySlug(categorySlug, PostStatus.PUBLISHED, pageable);

        return toPagedSummaryResponse(posts);
    }

    public PageResponse<PublicPostSummaryResponse> search(String query, int page, int size){


        String normalizedQuery = query == null ? "" : query.trim();

        if(normalizedQuery.isBlank()){
            throw new IllegalArgumentException("O parametro de busca não pode estar vazio");
        }

        Pageable pageable = buildPageable(page, size);
        Page<Post> posts = postRepository.searchPublished(query.trim(), PostStatus.PUBLISHED, pageable);

        return toPagedSummaryResponse(posts);
    }


    private Pageable buildPageable(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);
        return PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "publishedAt"));
    }

    private PageResponse<PublicPostSummaryResponse> toPagedSummaryResponse(Page<Post> posts) {
        return new PageResponse<>(
                posts.getContent().stream().map(mapper::toSummaryResponse).toList(),
                posts.getNumber(),
                posts.getSize(),
                posts.getTotalElements(),
                posts.getTotalPages(),
                posts.isFirst(),
                posts.isLast()
        );
    }
}
