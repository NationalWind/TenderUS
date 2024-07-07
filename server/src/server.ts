import express from "express"
import bodyParser from "body-parser";
import "dotenv/config";
import authRouter from "./routers/authRouter";

const app = express();


app.use(bodyParser.urlencoded({ limit: "30mb", extended: true }));

app.use(express.json());

app.use("/api/auth", authRouter)

app.listen(process.env.PORT, () => console.log("Server is running..."))