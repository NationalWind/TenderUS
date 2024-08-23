import express from "express";

import authMiddeware from "../middlewares/authMiddleware";
import profileController from "../controllers/profileController";

const profileRouter = express.Router();

profileRouter.get("/prof", authMiddeware.checkUser, profileController.getProf);
profileRouter.get("/pref", authMiddeware.checkUser, profileController.getPref);
profileRouter.post("/Pref", authMiddeware.checkUser, profileController.upsertPref);
profileRouter.post("/Prof", authMiddeware.checkUser, profileController.upsertProf);


export default profileRouter;

