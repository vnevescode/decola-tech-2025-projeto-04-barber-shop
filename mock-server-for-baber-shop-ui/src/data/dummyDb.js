// src/data/dummyDB.js
// "Banco" em memória para testes

// Lista de clientes
let clients = [
    {
        id: 11110000,
        name: "Ana Santos",
        email: "ana@example.com",
        phone: "21922223333",
    },
    {
        id: 11110001,
        name: "Fernando Almeida",
        email: "fernando@example.com",
        phone: "11922334455",
    },
    // ... adicione mais se quiser
];

// Apenas 1 "mês" de agenda de exemplo
let scheduleMonth = {
    year: 2025,
    month: 3,
    scheduledAppointments: [
        {
            id: 22220000,
            day: 10,
            startAt: "2025-03-10T09:00:00",
            endAt: "2025-03-10T10:00:00",
            clientId: 11110000,
            clientName: "Ana Santos",
        },
        {
            id: 22220001,
            day: 10,
            startAt: "2025-03-10T11:00:00",
            endAt: "2025-03-10T12:00:00",
            clientId: 0,
            clientName: "Maria Oliveira",
        },
        // ... insira mais agendamentos se quiser
    ],
};

// Exportamos para usar em outros módulos
module.exports = {
    clients,
    scheduleMonth,
};
