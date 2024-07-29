import express from "express";

import authMiddeware from "../middlewares/authMiddleware";
import messageController from "../controllers/messageController";

const messageRouter = express.Router();

messageRouter.post("/send", authMiddeware.checkUser, messageController.send);

export default messageRouter;

