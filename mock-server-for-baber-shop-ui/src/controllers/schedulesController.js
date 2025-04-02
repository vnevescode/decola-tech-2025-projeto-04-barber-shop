// src/controllers/schedulesController.js
const { scheduleMonth } = require("../data/dummyDb");

// GET /schedules/:year/:month
function getScheduleByYearMonth(req, res) {
    const yearParam = Number(req.params.year);
    const monthParam = Number(req.params.month);

    // Simples check: se bater com o scheduleMonth em memória, retornamos
    if (
        scheduleMonth.year === yearParam &&
        scheduleMonth.month === monthParam
    ) {
        return res.json(scheduleMonth);
    } else {
        return res
            .status(404)
            .json({ error: "Não há agendamentos para este ano/mês." });
    }
}

// POST /schedules -> cria um novo agendamento
function createSchedule(req, res) {
    const { day, startAt, endAt, clientId, clientName } = req.body;

    if (!day || !startAt || !endAt) {
        return res
            .status(400)
            .json({ error: "Campos obrigatórios: day, startAt, endAt." });
    }

    // Gera ID de 8 dígitos
    const newId = Math.floor(20000000 + Math.random() * 70000000);

    const newSchedule = {
        id: newId,
        day,
        startAt,
        endAt,
        clientId: clientId || 0,
        clientName: clientName || "Anônimo",
    };

    scheduleMonth.scheduledAppointments.push(newSchedule);

    return res.status(201).json(newSchedule);
}

// DELETE /schedules/:id -> remove agendamento do array
function deleteSchedule(req, res) {
    const scheduleId = Number(req.params.id);

    const index = scheduleMonth.scheduledAppointments.findIndex(
        (a) => a.id === scheduleId
    );
    if (index < 0) {
        return res.status(404).json({ error: "Agendamento não encontrado." });
    }

    scheduleMonth.scheduledAppointments.splice(index, 1);
    return res.status(204).send();
}

module.exports = {
    getScheduleByYearMonth,
    createSchedule,
    deleteSchedule,
};
