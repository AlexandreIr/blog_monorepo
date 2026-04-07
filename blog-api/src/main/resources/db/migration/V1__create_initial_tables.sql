CREATE TABLE tb_users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(120) NOT NULL,
                       email VARCHAR(150) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL
);

CREATE TABLE tb_category (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL UNIQUE,
                            description TEXT NOT NULL,
                            slug VARCHAR(120) NOT NULL UNIQUE,
                            created_at TIMESTAMP NOT NULL,
                            updated_at TIMESTAMP NOT NULL
);

CREATE TABLE tb_posts (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(180) NOT NULL,
                       slug VARCHAR(200) NOT NULL UNIQUE,
                       summary VARCHAR(300) NOT NULL,
                       content TEXT NOT NULL,
                       cover_image_url VARCHAR(500),
                       meta_title VARCHAR(255),
                       meta_description VARCHAR(300),
                       status VARCHAR(20) NOT NULL,
                       published_at TIMESTAMP,
                       author_id BIGINT NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL,
                       CONSTRAINT fk_posts_author FOREIGN KEY (author_id) REFERENCES tb_users(id)
);

CREATE TABLE post_categories (
                                 post_id BIGINT NOT NULL,
                                 category_id BIGINT NOT NULL,
                                 PRIMARY KEY (post_id, category_id),
                                 CONSTRAINT fk_post_categories_post FOREIGN KEY (post_id) REFERENCES tb_posts(id) ON DELETE CASCADE,
                                 CONSTRAINT fk_post_categories_category FOREIGN KEY (category_id) REFERENCES tb_category(id) ON DELETE CASCADE
);

CREATE TABLE tb_comment (
                          id BIGSERIAL PRIMARY KEY,
                          author_name VARCHAR(120) NOT NULL,
                          author_email VARCHAR(150) NOT NULL,
                          content TEXT NOT NULL,
                          status VARCHAR(20) NOT NULL,
                          post_id BIGINT NOT NULL,
                          parent_id BIGINT,
                          likes_count INTEGER NOT NULL,
                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP NOT NULL,
                          CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES tb_posts(id) ON DELETE CASCADE
);

CREATE TABLE comment_likes (
                               id BIGSERIAL PRIMARY KEY,
                               comment_id BIGINT NOT NULL,
                               ip_address VARCHAR(150) NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);