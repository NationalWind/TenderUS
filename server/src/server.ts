import express from "express"
import bodyParser from "body-parser";
import "dotenv/config";
import authRouter from "./routers/authRouter";
import swipeRouter from "./routers/swipeRouter";
import { AdmGetAuth } from "./lib/firebase";
import https from "https";
import fs from "node:fs";

const app = express();

app.use(bodyParser.urlencoded({ limit: "30mb", extended: true }));

app.use(express.json());

app.use("/api/auth", authRouter);
app.use("/api/swipe", swipeRouter);

const options = {
  key: fs.readFileSync("cert/key.pem"),
  cert: fs.readFileSync("cert/cert.pem"),
}

https.createServer(options, app).listen(process.env.PORT, () => console.log("Server is running..."));