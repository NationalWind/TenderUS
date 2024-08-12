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

const messagePollers: { [key: string]: { res: Response, timeout: NodeJS.Timeout } } = {};


const messageController = {
  // GET /api/message/matches
  getMatches: async (req: Request, res: Response) => {
    try {
      const username = req.body.username;

      const matches = await db.match.aggregateRaw({
        pipeline: [
          {
            $match: {
              $or: [
                { user1: username },
                { user2: username }
              ]
            }
          },

          {
            $addFields: {
              id:
              {
                $cond: {
                  if: { $eq: ["$user1", username] },
                  then: "$user2",
                  else: "$user1"
                }
              },
              user1_user2: {
                $concat: [
                  "$user1",
                  "_",
                  "$user2"
                ]
              }
            }
          },
          {
            $lookup: {
              from: "Profile",
              localField: "id",
              foreignField: "username",
              as: "userInfoArr"
            }
          },

          {
            $lookup: {
              from: "Conversation",
              localField: "user1_user2",
              foreignField: "user1_user2",
              as: "conversationArr"
            }
          },

          {
            $addFields: {
              userInfo:
              {
                $arrayElemAt: ["$userInfoArr", 0]
              },
              conversation:
              {
                $arrayElemAt: ["$conversationArr", 0]
              },
            }
          },
          {
            $addFields: {
              convoID: "$conversation._id"
            }
          },
          {
            $lookup: {
              from: "Message",
              localField: "convoID",
              foreignField: "conversationID",
              pipeline: [
                {
                  $addFields: {
                    _id: {
                      $toString: "$_id"
                    },
                    createdAt: {
                      $toString: "$createdAt"
                    },
                    conversationID: {
                      $toString: "$conversationID"
                    }
                  }
                },
                {
                  $limit: 20
                }
              ],
              as: "messageArr",
            }
          },
          {
            $project: {
              _id: 0,
              username: "$userInfo.username",
              createdAt: {
                $toString: "$createdAt"
              },
              avatarIcon: "$userInfo.avatarIcon",
              displayName: "$userInfo.displayName",
              isActive: "$userInfo.isActive",
              isRead: "$conversation.isRead",
              messageArr: 1
            }
          }



        ]
      });
      res.status(200).json(matches);
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },

  // GET /api/message/polling
  messageLongPoll: async (req: Request, res: Response) => {
    try {
      const username = req.body.username;
      if (messagePollers[username]) {
        clearTimeout(messagePollers[username].timeout);
      }
      messagePollers[username] = {
        res, timeout: setTimeout(() => {
          res.status(408).json({ message: "Timeout" });
          delete messagePollers[username];
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

      if (req.body.msgType !== "Text" && req.body.msgType !== "Image" && req.body.msgType !== "Audio") {
        res.status(400).json({ message: "Bad request: invalid message type" });
        return;
      }


      var user1 = req.body.username
      var user2 = req.body.receiver
      if (req.body.username > req.body.receiver) {
        user1 = req.body.receiver
        user2 = req.body.username
      }

      const match = await db.match.findFirst({
        where: {
          user1: user1,
          user2: user2
        }
      });
      if (!match) {
        res.status(403).json({ message: "You haven't got a match with this user" });
        return;
      }
      var data: Omit<Message, "doc_id">;
      const converation = await db.conversation.findFirst({
        where: {
          user1_user2: user1 + "_" + user2
        }
      });

      if (!converation) {
        const convo = await db.conversation.create({
          data: {
            user1_user2: user1 + "_" + user2,
            isRead: false
          }
        });

        data = {
          conversationID: convo.doc_id,
          msgID: 0,
          sender: req.body.username,
          receiver: req.body.receiver,
          msgType: req.body.msgType,
          content: req.body.content,
          createdAt: new Date(),
        };
        await db.message.create({ data });
      } else {
        const msg = await db.message.findFirst({
          where: {
            conversationID: converation.doc_id
          }
        });
        console.log(msg, converation, typeof (converation.doc_id))
        data = {
          conversationID: converation.doc_id,
          msgID: msg!.msgID + 1,
          sender: req.body.username,
          receiver: req.body.receiver,
          msgType: req.body.msgType,
          content: req.body.content,
          createdAt: new Date(),
        };
        await db.message.create({ data });

      }
      await db.conversation.update({
        where: {
          doc_id: data.conversationID
        },
        data: {
          isRead: false
        }
      });
      /*await */FCMPendingMessage(data);

      if (messagePollers[data.receiver]) {
        clearTimeout(messagePollers[data.receiver].timeout);
        try {
          messagePollers[data.receiver].res.status(200).json(data);
        } catch (error) {
          console.log(error);
        }
        delete messagePollers[data.receiver];
      }

      res.status(200).json(data);

    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },

  // GET /api/message?receiver=&&page_size=&msgID=
  loadMessage: async (req: Request, res: Response) => {
    try {
      const receiver = req.query.receiver;
      if (!req.query.page_size || !receiver || typeof receiver !== "string" || typeof req.query.page_size !== "string") {
        res.status(400).json({ message: "Bad request" });
        return;
      }
      const page_size = parseInt(req.query.page_size);;
      const sender = req.body.username;

      var messages: Message[] = [];
      var user1 = sender
      var user2 = receiver
      if (sender > receiver) {
        user1 = receiver
        user2 = sender
      }
      const conversation = await db.conversation.findFirst({
        where: {
          user1_user2: user1 + "_" + user2
        }
      })
      if (!conversation) {
        res.status(200).json([]);
        return
      }
      if (req.query.msgID) {
        if (typeof req.query.msgID !== "string") {
          res.status(400).json({ message: "Bad request" });
          return;
        }

        messages = await db.message.findMany({
          where: {
            conversationID: conversation.doc_id,
            msgID: {
              lt: parseInt(req.query.msgID)
            }
          },
          take: page_size
        });
      } else {
        messages = await db.message.findMany({
          where: {
            conversationID: conversation.doc_id
          },
          take: page_size
        });
      }

      res.status(200).json(messages)
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },

  //api/message/read
  haveRead: async (req: Request, res: Response) => {
    try {
      const { conversationID } = req.body;
      if (!conversationID) {
        res.status(400).json({ message: "Where is my conversationID?" });
        return;
      }
      await db.conversation.update(
        {
          where: { doc_id: conversationID },
          data: {
            isRead: true
          }
        }
      )
      res.status(200).json({ message: "OK" });
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },
}


export default messageController;
