ALTER TABLE tb_users
    ADD COLUMN two_factor_enabled BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE tb_users
    ADD COLUMN two_factor_secret VARCHAR(120);

ALTER TABLE tb_users
    ADD COLUMN two_factor_pending_secret VARCHAR(120);