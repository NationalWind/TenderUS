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

const pollers: { [key: string]: { res: Response, timeout: NodeJS.Timeout } } = {};

const messageController = {
  longPoll: async (req: Request, res: Response) => {
    try {
      const username = req.body.id;
      if (pollers[username]) {
        clearTimeout(pollers[username].timeout);
      }
      pollers[username] = {
        res, timeout: setTimeout(() => {
          res.status(408).json({ message: "Timeout" });
          delete pollers[username];
        }, 30000)
      };
    } catch (error) {
      res.status(500).json({ message: "Something went wrong" });
    }

  },

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

      if (req.body.msgType !== "text" && req.body.msgType !== "image" && req.body.msgType !== "audio") {
        res.status(400).json({ message: "Bad request: invalid message type" });
        return;
      }

      const data: Omit<Message, "doc_id"> = {
        sender: req.body.id,
        receiver: req.body.receiver,
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

      if (pollers[data.receiver]) {
        clearTimeout(pollers[data.receiver].timeout);
        try {
          pollers[data.receiver].res.status(200).json(message);
        } catch (error) {
          console.log(error);
        }
        delete pollers[data.receiver];
      }

      res.status(200).json(message);

    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },

  // GET /api/message?receiver=&&page_size=&doc_id=
  loadMessage: async (req: Request, res: Response) => {
    try {
      const receiver = req.query.receiver;
      if (!req.query.page_size || !receiver || typeof receiver !== "string" || typeof req.query.page_size !== "string") {
        res.status(400).json({ message: "Bad request" });
        return;
      }
      const page_size = parseInt(req.query.page_size);;
      const sender = req.body.id;

      var messages: Message[] = [];

      if (req.query.doc_id) {
        if (typeof req.query.doc_id !== "string") {
          res.status(400).json({ message: "Bad request" });
          return;
        }
        messages = await db.message.findMany({
          where: {
            OR: [
              { sender: sender, receiver: receiver },
              { sender: receiver, receiver: sender },
            ],
          },
          skip: 1,
          take: page_size,
          orderBy: {
            doc_id: "desc"
          },
          cursor: {
            doc_id: req.query.doc_id
          },

        });
      } else {
        messages = await db.message.findMany({
          where: {
            OR: [
              { sender: sender, receiver: receiver },
              { sender: receiver, receiver: sender },
            ],
          },
          take: page_size,
          orderBy: {
            doc_id: "desc"
          }
        });
      }

      res.status(200).json(messages)
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },
}


export default messageController;
