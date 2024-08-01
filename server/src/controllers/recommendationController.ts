import { Request, Response } from "express";
import db from "../lib/db";
import { Account, Role, Profile, Preference } from "@prisma/client";
import { firebaseFCM } from "../lib/firebase";
import { parse } from "dotenv";

const recommendationController = {
  // GET /api/recommendation?page=&limit=
  getRecs: async (req: Request, res: Response) => {
    try {
      if (!req.query.page || !req.query.limit) {
        res.status(400).json({ message: "Bad request" });
        return;
      }

      const page = parseInt(req.query.page as string);
      const limit = parseInt(req.query.limit as string);

      const cur_pref = await db.preference.findUnique({ where: { username: req.body.id } });
      const cur_prof = await db.profile.findUnique({ where: { username: req.body.id } });

      if (!cur_prof || !cur_pref) {
        res.status(404).json({ message: "User requesting not found" });
        return;
      }

      // Preferences enforcement
      const users = await db.profile.findMany({
        where: {
          username: {
            not: cur_prof.username
          },
          age: {
            gte: cur_pref.ageMin,
            lte: cur_pref.ageMax
          }
        }
      });

      const recs: Profile[] = [];
      for (const user of users) {
        if ((cur_prof.longitude - user.longitude) * (cur_prof.longitude - user.longitude) + (cur_prof.latitude - user.latitude) * (cur_prof.latitude - user.latitude) <= cur_pref.maxDist * cur_pref.maxDist) {
          recs.push(user);
        }
      }

      // Recommend by interests
      recs.sort((a: Profile, b: Profile) => {
        const a_cur = a.interests.filter(value => cur_prof.interests.includes(value));
        const b_cur = b.interests.filter(value => cur_prof.interests.includes(value));
        return b_cur.length - a_cur.length;
      })

      const l = (page * 10) % recs.length
      const r = (page * 10 + limit) % recs.length
      if (l > r) {
        res.status(200).json({ profiles: recs.slice(l, recs.length).concat(recs.slice(0, r + 1)) });
      } else {
        res.status(200).json({ profiles: recs.slice(l, r + 1) });
      }
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },
};

export default recommendationController;
