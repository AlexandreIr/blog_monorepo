CREATE TABLE post_views (
                            id BIGSERIAL PRIMARY KEY,
                            post_id BIGINT NOT NULL,
                            ip_hash VARCHAR(128) NOT NULL,
                            created_at TIMESTAMP NOT NULL,
                            updated_at TIMESTAMP NOT NULL,
                            CONSTRAINT fk_post_views_post FOREIGN KEY (post_id) REFERENCES tb_posts(id) ON DELETE CASCADE,
                            CONSTRAINT uk_post_views_post_ip UNIQUE (post_id, ip_hash)
);

ALTER TABLE tb_posts
    ADD COLUMN view_count BIGINT NOT NULL DEFAULT 0;