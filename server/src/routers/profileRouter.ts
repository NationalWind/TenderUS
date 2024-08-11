import express from "express";

import authMiddeware from "../middlewares/authMiddleware";
import profileController from "../controllers/profileController";

const profileRouter = express.Router();

profileRouter.post("/uPref", authMiddeware.checkUser, profileController.updatePref);
profileRouter.post("/uProf", authMiddeware.checkUser, profileController.updateProf);
profileRouter.post("/cPref", authMiddeware.checkUser, profileController.createPref);
profileRouter.post("/cProf", authMiddeware.checkUser, profileController.createProf);

export default profileRouter;

