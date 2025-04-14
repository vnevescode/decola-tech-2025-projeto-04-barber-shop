-- V20250406225522__create_table_tb_users.sql generated in C:\PROJETO 04 - Barber Shop - NOVO\barber-shop-api (1)\barber-shop-api\src\main\resources\db\migration
-- Criação da tabela de usuários para autenticação
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE tb_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN'))
);
