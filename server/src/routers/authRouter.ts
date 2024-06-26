import express from "express";
import authController from "../controllers/authController";
import authMiddeware from "../middlewares/authMiddleware";

const authRouter = express.Router();

authRouter.get("/getInfo", authMiddeware.checkIfRightUser, authController.getInfo);
authRouter.post("/register", authController.register);
authRouter.post("/login", authController.login);

export default authRouter;
