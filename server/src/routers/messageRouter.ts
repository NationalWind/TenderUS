import express from "express";

import authMiddeware from "../middlewares/authMiddleware";
import messageController from "../controllers/messageController";

const messageRouter = express.Router();

messageRouter.post("/", authMiddeware.checkUser, messageController.send);
messageRouter.get("/poll", authMiddeware.checkUser, messageController.longPoll);
messageRouter.get("/", authMiddeware.checkUser, messageController.loadMessage);


export default messageRouter;

