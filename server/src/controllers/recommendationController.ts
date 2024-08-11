import { Request, Response } from "express";
import db from "../lib/db";
import { Account, Role, Profile, Preference } from "@prisma/client";
import { firebaseFCM } from "../lib/firebase";
import { parse } from "dotenv";

const validGroups = ["Free Tonight", "Study Group", "Open Day", "Binge Watchers", "Self Care"]

const recommendationController = {
  // GET /api/recommendation/groups
  getGroups: async (req: Request, res: Response) => {
    try {
      res.status(200).json({ groups: validGroups });
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },

  // POST /api/recommendation/join { id(username): String, group: String }
  join: async (req: Request, res: Response) => {
    try {
      const username: string = req.body.id;
      if (!req.body.group || typeof req.body.group !== "string" || !validGroups.includes(req.body.group)) {
        res.status(400).json({ message: "Bad request" });
        return;
      }
      const group: string = req.body.group;


      await db.profile.update({
        where: { username },
        data: {
          groups: {
            push: group
          }
        }
      });

      res.status(200).json({ message: "OK" });
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },
  // GET /api/recommendation?limit=&group=
  getRecs: async (req: Request, res: Response) => {
    try {
      if (!req.query.limit || typeof req.query.limit !== "string") {
        res.status(400).json({ message: "Bad request: limit param is missing" });
        return;
      }

      if (req.query.group && typeof req.query.group !== "string") {
        res.status(400).json({ message: "Bad request" });
        return;
      }

      const limit = parseInt(req.query.limit);

      if (limit <= 0) {
        res.status(400).json({ message: "Bad request: limit must be greater than 0" });
        return;
      }

      const cur_pref = await db.preference.findUnique({ where: { username: req.body.id } });
      const cur_prof = await db.profile.findUnique({ where: { username: req.body.id } });

      if (!cur_prof || !cur_pref) {
        res.status(404).json({ message: "User requesting not found" });
        return;
      }

      if (req.query.group && !cur_prof.groups.includes(req.query.group)) {
        res.status(403).json({ message: "You must join the group first" });
        return
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
          if (req.query.group) {
            if (user.groups.includes(req.query.group)) {
              recs.push(user);
            }
          } else {
            recs.push(user);
          }
        }
      }

      // Recommend by interests
      recs.sort((a: Profile, b: Profile) => {
        const a_cur = a.interests.filter(value => cur_prof.interests.includes(value));
        const b_cur = b.interests.filter(value => cur_prof.interests.includes(value));
        return b_cur.length - a_cur.length;
      })

      if (!cur_pref.recPage) {
        cur_pref.recPage = 0;
        await db.preference.update({
          where: { username: req.body.id },
          data: {
            recPage: 1
          }
        });
      } else {
        await db.preference.update({
          where: { username: req.body.id },
          data: {
            recPage: (cur_pref.recPage + 1) % 1e9
          }
        });
      }

      const resRecs: Profile[] = [];

      const begin = cur_pref.recPage * 10;
      const end = cur_pref.recPage * 10 + limit;
      for (let i = begin; i < end; i++) {
        resRecs.push(recs[i % recs.length]);
      }
      res.status(200).json({ profiles: resRecs });
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },
};

export default recommendationController;
