INSERT INTO tb_users (name, email, password, role, created_at, updated_at)
VALUES (
           'Administrador',
           'admin@empresa.com',
           '$2a$10$/O/Z6E/FSRgkuRR/.4XCiOafMgVOyrqgpBOY/KYmxWPueswC9Ap6e',
           'ADMIN',
           NOW(),
           NOW()
       );

INSERT INTO tb_category (name, description, slug, created_at, updated_at)
VALUES (
           'Facilities',
           'Facitilites',
           'facilities',
           NOW(),
           NOW()
       );