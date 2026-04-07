INSERT INTO tb_users (name, email, password, role, created_at, updated_at)
VALUES (
           'Administrador',
           'admin@empresa.com',
           '$2a$10$DowJonesxYx1V8X3r0xR5nO4JxYxQ7bLxM6sKfQ9mM0Yw2iV3e6QmK',
           'ADMIN',
           NOW(),
           NOW()
       );

INSERT INTO tb_category (name, description, slug, created_at, updated_at)
VALUES (
        'Tecnologia',
        'Categoria de tecnologias no geral',
        'tech',
        NOW(),
        NOW()
       );