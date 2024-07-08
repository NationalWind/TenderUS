import { Request, Response } from "express";
import jwt from "jsonwebtoken";
import db from "../lib/db";
import { Account, Role, Like } from "@prisma/client";
import { getMessaging } from "../lib/firebase";

const MatchFCM = async (username: string) => {
  const foundAccount = await db.account.findUnique({ where: { username: username } });

  if (foundAccount!.FCMRegToken === null) return;

  const registrationToken = foundAccount!.FCMRegToken;

  const message = {
    data: {
      message: "You have a new match!",
    },
    token: registrationToken
  };

  // Send a message to the device corresponding to the provided
  // registration token.
  await getMessaging().send(message)
};

// POST /like {token: String, likedID: String}
const swipeController = {
  like: async (req: Request, res: Response) => {
    try {
      interface LikeRequest {
        id: string;
        likedID: string;
      }
      const data: LikeRequest = req.body;

      if (!data.id || !data.likedID || data.id === data.likedID) {
        res.status(400).json({ message: "Bad request" });
        return;
      }


      await db.like.create({ data });
      const checked = await db.like.findFirst({ where: { id: data.likedID, likedID: data.id } });
      var match = false;
      if (checked) {
        match = true;
        MatchFCM(data.likedID);
      }

      res.status(200).json({ match });
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },

  // POST /pass {token: String, passedID: String}
  pass: async (req: Request, res: Response) => {
    try {
      interface PassRequest {
        id: string;
        passedID: string;
      }
      const data: PassRequest = req.body;

      if (!data.id || !data.passedID || data.id === data.passedID) {
        res.status(400).json({ message: "Bad request" });
        return;
      }

      await db.pass_A.create({ data });
      res.status(200).json({ message: "Pass recorded" });
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  }
};

export default swipeController;
