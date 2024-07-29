import { Request, Response } from "express";
import db from "../lib/db";
import { Account, Role, Message } from "@prisma/client";
import { parse } from "dotenv";



const messageController = {
  // POST /send {Message}
  send: async (req: Request, res: Response) => {
    try {

    } catch (error) {

    }
  },

  // GET /messages/:sender/:receiver
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
  // GET /messages/:sender/:receiver/:msgID
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
