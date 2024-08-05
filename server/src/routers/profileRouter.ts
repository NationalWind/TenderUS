import express from "express";

import authMiddeware from "../middlewares/authMiddleware";
import profileController from "../controllers/profileController";

const profileRouter = express.Router();

profileRouter.post("/", authMiddeware.checkUser, profileController.update);

export default profileRouter;

