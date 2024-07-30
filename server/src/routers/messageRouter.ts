import express from "express";

import authMiddeware from "../middlewares/authMiddleware";
import messageController from "../controllers/messageController";

const messageRouter = express.Router();

messageRouter.post("/send", authMiddeware.checkUser, messageController.send);
messageRouter.post("/messages/:sender/:receiver", authMiddeware.checkUser, messageController.getLastMessage);
messageRouter.post("/messages/:sender/:receiver/:msgID", authMiddeware.checkUser, messageController.getMessage);

export default messageRouter;

