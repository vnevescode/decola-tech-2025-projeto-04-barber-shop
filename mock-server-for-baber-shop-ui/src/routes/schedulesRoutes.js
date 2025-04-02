// src/routes/schedulesRoutes.js
const express = require("express");
const router = express.Router();
const {
    getScheduleByYearMonth,
    createSchedule,
    deleteSchedule,
} = require("../controllers/schedulesController");

// GET /schedules/:year/:month
router.get("/:year/:month", getScheduleByYearMonth);

// POST /schedules
router.post("/", createSchedule);

// DELETE /schedules/:id
router.delete("/:id", deleteSchedule);

module.exports = router;
