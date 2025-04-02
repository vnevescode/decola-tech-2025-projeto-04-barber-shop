// src/controllers/clientsController.js
const { clients } = require("../data/dummyDb");

function getAllClients(req, res) {
    return res.json(clients);
}

function getClientById(req, res) {
    const id = Number(req.params.id);
    const client = clients.find((c) => c.id === id);
    if (!client) {
        return res.status(404).json({ error: "Cliente não encontrado." });
    }
    return res.json(client);
}

function createClient(req, res) {
    const { name, email, phone } = req.body;

    if (!name || !email || !phone) {
        return res
            .status(400)
            .json({ error: "Campos obrigatórios: name, email, phone." });
    }

    // Gera ID simples (8 dígitos aleatórios)
    const newId = Math.floor(10000000 + Math.random() * 90000000);
    const newClient = { id: newId, name, email, phone };
    clients.push(newClient);

    return res.status(201).json(newClient);
}

function updateClient(req, res) {
    const id = Number(req.params.id);
    const { name, email, phone } = req.body;

    const index = clients.findIndex((c) => c.id === id);
    if (index < 0) {
        return res.status(404).json({ error: "Cliente não encontrado." });
    }

    if (name) clients[index].name = name;
    if (email) clients[index].email = email;
    if (phone) clients[index].phone = phone;

    return res.json(clients[index]);
}

function deleteClient(req, res) {
    const id = Number(req.params.id);
    const index = clients.findIndex((c) => c.id === id);
    if (index < 0) {
        return res.status(404).json({ error: "Cliente não encontrado." });
    }

    clients.splice(index, 1);
    return res.status(204).send();
}

// Exporta as funções
module.exports = {
    getAllClients,
    getClientById,
    createClient,
    updateClient,
    deleteClient,
};
