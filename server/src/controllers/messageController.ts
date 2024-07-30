import { Request, Response } from "express";
import db from "../lib/db";
import { Account, Role, Message } from "@prisma/client";
import { firebaseFCM } from "../lib/firebase";
import { parse } from "dotenv";

const FCMPendingMessage = async (data: Message) => {
  const foundAccount = await db.account.findUnique({ where: { username: data.receiver } });

  if (foundAccount!.FCMRegToken === null) return;

  const registrationToken = foundAccount!.FCMRegToken;

  await firebaseFCM.sendFCM(registrationToken, "You have a new message!");
};

const messageController = {
  // POST /api/message/ {message: Message}
  // Must post multimedia first then use this endpoint with content = url
  send: async (req: Request, res: Response) => {
    try {
      const data: Message = req.body;
      if (!data.sender || !data.receiver || !data.msgID || !data.content) {
        res.status(400).json({ message: "Bad request" });
        return
      }
      const match = await db.match.findFirst({
        where: {
          OR: [
            { id1: data.sender, id2: data.receiver },
            { id1: data.receiver, id2: data.sender }
          ]
        }
      });
      if (!match) {
        res.status(403).json({ message: "You haven't got a match with this user" });
        return;
      }
      const message = await db.message.create({ data });
      res.status(200).json(message);
      await FCMPendingMessage(data);
    } catch (error) {
      res.status(500).json({ message: "Something went wrong" });
    }
  },

  // GET /api/message/:sender/:receiver
  getLastMessage: async (req: Request, res: Response) => {
    try {
      const { sender, receiver } = req.params;
      const message = await db.message.findFirst({
        where: {
          OR: [
            { sender: sender, receiver: receiver },
            { sender: receiver, receiver: sender },
          ],
        }
      });
      if (!message) {
        res.status(404).json({ message: "Message not found" });
        return;
      }
      res.status(200).json(message)
    } catch (error) {
      res.status(500).json({ message: "Something went wrong" });
    }
  },
  // GET /api/message/:sender/:receiver/:msgID
  getMessage: async (req: Request, res: Response) => {
    try {
      const { sender, receiver, msgID } = req.params;
      const message = await db.message.findFirst({
        where: {
          OR: [
            { sender: sender, receiver: receiver, msgID: parseInt(msgID) },
            { sender: receiver, receiver: sender, msgID: parseInt(msgID) }
          ]
        }
      });
      if (!message) {
        res.status(404).json({ message: "Message not found" });
        return;
      }
      res.status(200).json(message);
    } catch (error) {
      res.status(500).json({ message: "Something went wrong" });
    }
  },

};

export default messageController;
