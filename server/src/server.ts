import express from "express"
import bodyParser from "body-parser";
import "dotenv/config";
import authRouter from "./routers/authRouter";
import swipeRouter from "./routers/swipeRouter";
import { AdmGetAuth } from "./lib/firebase";

const app = express();

app.use(bodyParser.urlencoded({ limit: "30mb", extended: true }));

app.use(express.json());

app.use("/api/auth", authRouter);
app.use("/api/swipe", swipeRouter);

app.listen(process.env.PORT, () => console.log("Server is running..."))