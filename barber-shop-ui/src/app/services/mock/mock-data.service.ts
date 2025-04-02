import { InMemoryDbService } from 'angular-in-memory-web-api';

export class MockDataService implements InMemoryDbService {
    createDb() {
        // Simula uma tabela de "clients"
        const clients = [
            {
                id: 1,
                name: 'João da Silva',
                email: 'joao@example.com',
                phone: '11999999999',
            },
            {
                id: 2,
                name: 'Maria Oliveira',
                email: 'maria@example.com',
                phone: '21988888888',
            },
            {
                id: 3,
                name: 'Carlos Souza',
                email: 'carlos@example.com',
                phone: '31955554444',
            },
            {
                id: 4,
                name: 'Ana Santos',
                email: 'ana@example.com',
                phone: '21922223333',
            },
            {
                id: 5,
                name: 'Beatriz Mendes',
                email: 'bea@example.com',
                phone: '11987654321',
            },
        ];

        // Simula uma "tabela" de agendamentos (schedules)
        const schedules = [
            {
                id: 101,
                day: 10,
                startAt: new Date('2025-05-10T09:00:00'),
                endAt: new Date('2025-05-10T10:00:00'),
                clientId: 1,
                clientName: 'João da Silva',
            },
            {
                id: 102,
                day: 10,
                startAt: new Date('2025-05-10T11:00:00'),
                endAt: new Date('2025-05-10T12:00:00'),
                clientId: 2,
                clientName: 'Maria Oliveira',
            },
            {
                id: 103,
                day: 10,
                startAt: new Date('2025-05-10T13:00:00'),
                endAt: new Date('2025-05-10T14:00:00'),
                clientId: 3,
                clientName: 'Carlos Souza',
            },
            {
                id: 201,
                day: 11,
                startAt: new Date('2025-05-11T09:00:00'),
                endAt: new Date('2025-05-11T10:00:00'),
                clientId: 4,
                clientName: 'Ana Santos',
            },
            {
                id: 202,
                day: 11,
                startAt: new Date('2025-05-11T10:00:00'),
                endAt: new Date('2025-05-11T11:00:00'),
                clientId: 5,
                clientName: 'Beatriz Mendes',
            },
        ];

        // O in-memory-web-api vai tratar "clients" e "schedules" como endpoints
        return { clients, schedules };
    }
}
