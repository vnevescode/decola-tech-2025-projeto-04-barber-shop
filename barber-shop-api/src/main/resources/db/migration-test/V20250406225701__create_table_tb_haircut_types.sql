-- V20250406225701__create_table_tb_haircut_types.sql generated in C:\PROJETO 04 - Barber Shop - NOVO\barber-shop-api (1)\barber-shop-api\src\main\resources\db\migration
-- Tabela para tipos de cortes com preço
CREATE TABLE tb_haircut_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    price NUMERIC(10, 2) NOT NULL CHECK (price >= 0 AND price <= 10000)
);