import { Request, Response } from "express";
import jwt from "jsonwebtoken";
import db from "../lib/db";
import { Account, Role, Like, Match } from "@prisma/client";
import { firebaseFCM } from "../lib/firebase";


const MatchFCM = async (username: string) => {
  const foundAccount = await db.account.findUnique({ where: { username: username } });

  if (foundAccount!.FCMRegToken === null) return;

  const registrationToken = foundAccount!.FCMRegToken;

  await firebaseFCM.sendFCM(registrationToken, "You have a new match!");
};

const matchPollers: { [key: string]: { res: Response, timeout: NodeJS.Timeout } } = {};

// POST /like {token: String, likedUsername: String}
const swipeController = {
  like: async (req: Request, res: Response) => {
    try {
      interface LikeRequest {
        username: string;
        likedUsername: string;
      }
      const data: LikeRequest = {
        username: req.body.username,
        likedUsername: req.body.likedUsername,
      }

      if (!data.username || !data.likedUsername || data.username === data.likedUsername) {
        res.status(400).json({ message: "Bad request" });
        return;
      }


      await db.like.create({ data });
      const checked = await db.like.findFirst({ where: { username: data.likedUsername, likedUsername: data.username } });
      var match = false;
      if (checked) {
        match = true;
        if (data.username > data.likedUsername) {
          await db.match.create({ data: { user1: data.likedUsername, user2: data.username, createdAt: new Date() } });
        } else {
          await db.match.create({ data: { user1: data.username, user2: data.likedUsername, createdAt: new Date() } });
        }
        /*await */MatchFCM(data.likedUsername);
        if (matchPollers[data.likedUsername]) {
          clearTimeout(matchPollers[data.likedUsername].timeout);
          try {
            matchPollers[data.likedUsername].res.status(200).json(data);
          } catch (error) {
            console.log(error);
          }
          delete matchPollers[data.likedUsername];
        }

      }

      res.status(200).json({ match });
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },

  // POST /pass {token: String, passedUsername: String}
  pass: async (req: Request, res: Response) => {
    try {
      interface PassRequest {
        username: string;
        passedUsername: string;
      }
      const data: PassRequest = {
        username: req.body.username,
        passedUsername: req.body.passedUsername
      }

      if (!data.username || !data.passedUsername || data.username === data.passedUsername) {
        res.status(400).json({ message: "Bad request" });
        return;
      }

      await db.pass_A.create({ data });
      res.status(200).json({ message: "Pass recorded" });
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },

  // GET /swipe/polling
  matchLongPoll: async (req: Request, res: Response) => {
    try {
      const username = req.body.username;
      if (matchPollers[username]) {
        clearTimeout(matchPollers[username].timeout);
      }
      matchPollers[username] = {
        res, timeout: setTimeout(() => {
          res.status(408).json({ message: "Timeout" });
          delete matchPollers[username];
        }, 30000)
      };
    } catch (error) {
      res.status(500).json({ message: "Something went wrong" });
    }
  }
};

export default swipeController;
