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
      delete req.body.role
      await db.profile.create({ data });
      res.status(200).json({ message: "OK" });
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },
  createPref: async (req: Request, res: Response) => {
    try {
      const data: Preference = req.body;
      delete req.body.role
      await db.preference.create({ data });
      res.status(200).json({ message: "OK" });
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },
  updateProf: async (req: Request, res: Response) => {
    try {
      const data: Omit<Profile, "doc_id"> = req.body;
      delete req.body.role
      await db.profile.update({
        where: {
          username: req.body.username
        },
        data
      });
      res.status(200).json({ message: "OK" });
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },
  updatePref: async (req: Request, res: Response) => {
    try {
      const data: Preference = req.body;
      delete req.body.role
      await db.preference.update({
        where: {
          username: req.body.username
        },
        data
      });
      res.status(200).json({ message: "OK" });
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },
};

export default profileController;
