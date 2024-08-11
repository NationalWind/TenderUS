
import express from "express";
import authController from "../controllers/authController";
import authMiddeware from "../middlewares/authMiddleware";

const authRouter = express.Router();

authRouter.get("/account", authMiddeware.checkUser, authController.getAccount);
authRouter.post("/register", authController.register);
authRouter.post("/login", authController.login);
authRouter.post("/signOut", authMiddeware.checkUser, authController.signOut);
authRouter.post("/resetPassword", authController.resetPassword);

// authRouter.post("/changePassword", authController.changePassword);

export default authRouter;
