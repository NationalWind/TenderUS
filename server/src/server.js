import express from "express";
import "dotenv/config";
import authRouter from "./routers/authRouter.js";

const app = express();

app.use("/api/auth", authRouter);

app.listen(process.env.PORT, async () => console.log("Server is running..."));
