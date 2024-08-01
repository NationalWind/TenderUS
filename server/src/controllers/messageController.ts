import { Request, Response } from "express";
import db from "../lib/db";
import { Account, Role, Message } from "@prisma/client";
import { firebaseFCM } from "../lib/firebase";
import { parse } from "dotenv";

const FCMPendingMessage = async (data: Omit<Message, "doc_id">) => {
  const foundAccount = await db.account.findUnique({ where: { username: data.receiver } });

  if (foundAccount!.FCMRegToken === null) return;

  const registrationToken = foundAccount!.FCMRegToken;

  await firebaseFCM.sendFCM(registrationToken, "You have a new message!");
};

const messageController = {
  // POST /api/message/ {message}
  // Must post multimedia first then use this endpoint with content = url
  send: async (req: Request, res: Response) => {
    try {
      const requiredFields = ["id", "receiver", "msgType", "content"];
      const missingFields = requiredFields.filter(field => !req.body[field]);

      if (missingFields.length > 0) {
        res.status(400).json({ message: `Bad request: missing fields ${missingFields.join(", ")}` });
        return;
      }

      const lastMsg = await db.message.findFirst({
        where: {
          OR: [
            { sender: req.body.id, receiver: req.body.receiver },
            { sender: req.body.receiver, receiver: req.body.id }
          ]
        }
      });
      const lastMsgID = lastMsg ? lastMsg.msgID : -1;

      const data: Omit<Message, "doc_id"> = {
        sender: req.body.id,
        receiver: req.body.receiver,
        msgID: lastMsgID + 1,
        msgType: req.body.msgType,
        content: req.body.content,
        createdAt: new Date(),
      };

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
      /*await */FCMPendingMessage(data);
      res.status(200).json(message);

    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },

  // GET /api/message/:receiver?page=&limit=
  getMessage: async (req: Request, res: Response) => {
    try {
      const { receiver } = req.params;
      if (!req.query.page || !req.query.limit) {
        res.status(400).json({ message: "Bad request: missing paging parameters" });
        return;
      }
      const page = parseInt(req.query.page as string);
      const limit = parseInt(req.query.limit as string);
      const sender = req.body.id;
      const messages = await db.message.findMany({
        where: {
          OR: [
            { sender: sender, receiver: receiver },
            { sender: receiver, receiver: sender },
          ],
        },
        skip: page * 10,
        take: limit
      });
      if (!messages) {
        res.status(404).json({ message: "Message not found" });
        return;
      }
      res.status(200).json(messages)
    } catch (error) {
      res.status(500).json({ message: "Something went wrong" });
    }
  },
}


export default messageController;
