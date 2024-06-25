import express from "express";
import authController from "../controllers/authController.js";
import authMiddeware from "../middlewares/authMiddleware.js";

const authRouter = express.Router();

authRouter.get("/getInfo", authMiddeware.checkIfRightUser, authController.getInfo);
authRouter.post("/register", authController.register);
authRouter.post("/login", authController.login);

export default authRouter;
