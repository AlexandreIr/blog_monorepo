CREATE TABLE post_views (
                            id BIGSERIAL PRIMARY KEY,
                            post_id BIGINT NOT NULL,
                            ip_hash VARCHAR(100) NOT NULL,
                            user_agent VARCHAR(500),
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT fk_post_views_post
                                FOREIGN KEY (post_id)
                                    REFERENCES tb_posts(id)
                                    ON DELETE CASCADE
);

CREATE INDEX idx_post_views_post_id ON post_views(post_id);
CREATE INDEX idx_post_views_ip_address ON post_views(ip_hash);