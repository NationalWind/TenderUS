import express from "express";

import authMiddeware from "../middlewares/authMiddleware";
import messageController from "../controllers/messageController";

const messageRouter = express.Router();

messageRouter.post("/", authMiddeware.checkUser, messageController.send);
messageRouter.get("/polling", authMiddeware.checkUser, messageController.messageLongPoll);

messageRouter.get("/", authMiddeware.checkUser, messageController.loadMessage);
messageRouter.get("/matches", authMiddeware.checkUser, messageController.getMatches);


export default messageRouter;

