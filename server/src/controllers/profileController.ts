import { Request, Response } from "express";
import jwt from "jsonwebtoken";
import db from "../lib/db";
import { Account, Role, Like, Match, Profile, Preference } from "@prisma/client";
import { firebaseFCM } from "../lib/firebase";


// POST /api/profile/
const profileController = {
  createProf: async (req: Request, res: Response) => {
    try {
      const data: Profile = req.body;
      await db.profile.create({ data });
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },
  createPref: async (req: Request, res: Response) => {
    try {
      const data: Preference = req.body;
      await db.preference.create({ data });
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },
  updateProf: async (req: Request, res: Response) => {
    try {
      const data: Profile = req.body;
      await db.profile.create({ data });
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },
  updatePref: async (req: Request, res: Response) => {
    try {
      const data: Preference = req.body;
      await db.preference.create({ data });
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },
};

export default profileController;
