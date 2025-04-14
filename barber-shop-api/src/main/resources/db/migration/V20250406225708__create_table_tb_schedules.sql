-- V20250406225708__create_table_tb_schedules.sql generated in C:\PROJETO 04 - Barber Shop - NOVO\barber-shop-api (1)\barber-shop-api\src\main\resources\db\migration
-- Tabela de agendamento
CREATE TABLE tb_schedules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    start_at TIMESTAMPTZ NOT NULL,
    end_at TIMESTAMPTZ NOT NULL,
    confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    canceled BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT now(),
    client_id UUID NOT NULL,
    haircut_type_id UUID NOT NULL,

    CONSTRAINT fk_tb_schedules_client FOREIGN KEY (client_id) REFERENCES tb_clients(id) ON DELETE CASCADE,
    CONSTRAINT fk_tb_schedules_haircut FOREIGN KEY (haircut_type_id) REFERENCES tb_haircut_types(id),
    CONSTRAINT uk_tb_schedules_slot UNIQUE (start_at, end_at)
);
