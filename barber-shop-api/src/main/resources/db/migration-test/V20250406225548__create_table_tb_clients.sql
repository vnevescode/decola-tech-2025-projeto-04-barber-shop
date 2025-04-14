-- V20250406225548__create_table_tb_clients.sql generated in C:\PROJETO 04 - Barber Shop - NOVO\barber-shop-api (1)\barber-shop-api\src\main\resources\db\migration
-- Tabela de perfil do cliente, vinculado a um user
CREATE TABLE tb_clients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL,
    phone VARCHAR(11) NOT NULL UNIQUE,
    user_id UUID NOT NULL UNIQUE,
    CONSTRAINT fk_tb_clients_user FOREIGN KEY (user_id) REFERENCES tb_users(id) ON DELETE CASCADE
);
