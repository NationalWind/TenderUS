import express from "express";
import authController from "../controllers/authController";
import authMiddeware from "../middlewares/authMiddleware";

const authRouter = express.Router();

authRouter.get("/account/:id", authMiddeware.checkUser, authController.getAccount);
authRouter.post("/register", authController.register);
authRouter.post("/login", authController.login);
authRouter.post("/changePassword", authController.changePassword);
authRouter.post("/resetPassword", authController.resetPassword);

export default authRouter;
