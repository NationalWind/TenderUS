import express from "express";
import bodyParser from "body-parser";
import "dotenv/config";
// import authRouter from "./routers/authRouter";
// import swipeRouter from "./routers/swipeRouter";
// import messageRouter from "./routers/messageRouter";
// import recommendationRouter from "./routers/recommendationRouter";
// import profileRouter from "./routers/profileRouter";
import adminRouter from "./routers/adminRouter";
// import { AdmGetAuth, firebaseFCM } from "./lib/firebase";
import https from "https";
import http from "http";
// import fs from "node:fs";

const app = express();

app.use(bodyParser.urlencoded({ limit: "30mb", extended: true }));

app.use(express.json());

// app.use("/api/auth", authRouter);
// app.use("/api/swipe", swipeRouter);
// app.use("/api/message", messageRouter);
// app.use("/api/recommendation", recommendationRouter);
// app.use("/api/profile", profileRouter);
app.use("/api/admin", adminRouter);

const options = {
  //   key: fs.readFileSync("cert/key.pem"),
  //   cert: fs.readFileSync("cert/cert.pem"),
};

http.createServer(app).listen(process.env.HTTP_PORT);
https
  .createServer(options, app)
  .listen(process.env.HTTPS_PORT, () => console.log("Server is running..."));
