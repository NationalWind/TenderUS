import express from "express";

import authMiddeware from "../middlewares/authMiddleware";
import recommendationController from "../controllers/recommendationController";

const recommendationRouter = express.Router();


recommendationRouter.get("/", authMiddeware.checkUser, recommendationController.getRecs);
recommendationRouter.get("/groups", authMiddeware.checkUser, recommendationController.getGroups);
recommendationRouter.post("/join", authMiddeware.checkUser, recommendationController.join);

export default recommendationRouter;

