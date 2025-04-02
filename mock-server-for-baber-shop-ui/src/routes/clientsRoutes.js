// src/routes/clientsRoutes.js
const express = require("express");
const router = express.Router();
const {
    getAllClients,
    getClientById,
    createClient,
    updateClient,
    deleteClient,
} = require("../controllers/clientsController");

// GET /clients
router.get("/", getAllClients);

// GET /clients/:id
router.get("/:id", getClientById);

// POST /clients
router.post("/", createClient);

// PUT /clients/:id
router.put("/:id", updateClient);

// DELETE /clients/:id
router.delete("/:id", deleteClient);

module.exports = router;
