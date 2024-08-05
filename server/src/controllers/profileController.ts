import { Request, Response } from "express";
import jwt from "jsonwebtoken";
import db from "../lib/db";
import { Account, Role, Like, Match } from "@prisma/client";
import { firebaseFCM } from "../lib/firebase";


// POST /api/profile/
const profileController = {
  update: async (req: Request, res: Response) => {
    try {

    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },
};

export default profileController;
