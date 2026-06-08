package br.com.libertadfacilities.blog.services.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class BlogMetricsService {

    private final MeterRegistry meterRegistry;

    public BlogMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void incrementPostView(String slug){
        Counter.builder("blog_post_views_total")
                .description("Total de visualizações de posts")
                .tag("slug", slug)
                .register(meterRegistry)
                .increment();
    }

    public void incrementCommentCreated(String slug) {
        Counter.builder("blog_comments_created_total")
                .description("Total de comentários enviados")
                .tag("slug", slug)
                .register(meterRegistry)
                .increment();
    }

    public void incrementPostPublished() {
        Counter.builder("blog_posts_published_total")
                .description("Total de posts publicados")
                .register(meterRegistry)
                .increment();
    }

    public void incrementPostCreated() {
        Counter.builder("blog_posts_created_total")
                .description("Total de posts criados")
                .register(meterRegistry)
                .increment();
    }
}
