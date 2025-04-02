// server.js
const express = require("express");
const cors = require("cors");
const app = express();
const clientRoutes = require("./src/routes/clientsRoutes");
const scheduleRoutes = require("./src/routes/schedulesRoutes");

app.use(cors());
app.use(express.json());

// Rotas de clients:
// /clients -> GET, POST, etc.
app.use("/clients", clientRoutes);

// Rotas de schedules:
// /schedules -> GET, POST, DELETE etc.
app.use("/schedules", scheduleRoutes);

// Sobe o servidor
const PORT = 3005;
app.listen(PORT, () => {
    console.log(`Servidor rodando em http://localhost:${PORT}`);
});
