import { Request, Response } from "express";
import db from "../lib/db";
import { Account, Role } from "@prisma/client";
import { firebaseFCM } from "../lib/firebase";
import { parse } from "dotenv";

const recommendationController = {
  // GET /api/recommendation/
  getRecs: async (req: Request, res: Response) => {
    try {
      res.status(200).json({ recommendation: "OK" });
    } catch (error) {
      res.status(500).json({ recommendation: "Something went wrong" });
    }
  },




};

export default recommendationController;
