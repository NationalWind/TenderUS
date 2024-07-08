import express from "express";

import authMiddeware from "../middlewares/authMiddleware";
import swipeController from "../controllers/swipeController";

const swipeRouter = express.Router();

swipeRouter.post("/like", authMiddeware.checkUser, swipeController.like);
swipeRouter.post("/pass", authMiddeware.checkUser, swipeController.pass);

export default swipeRouter;

