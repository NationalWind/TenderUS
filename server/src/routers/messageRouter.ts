import express from "express";

import authMiddeware from "../middlewares/authMiddleware";
import messageController from "../controllers/messageController";

const messageRouter = express.Router();

messageRouter.post("/", authMiddeware.checkUser, messageController.send);
messageRouter.get("/:receiver", authMiddeware.checkUser, messageController.getMessage);

export default messageRouter;

