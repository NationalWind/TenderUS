import { Request, Response } from "express";
import db from "../lib/db";
import { getAge } from "../lib/timeUtil";
import { Account, Role, Profile, Preference } from "@prisma/client";
import { firebaseFCM } from "../lib/firebase";
import { parse } from "dotenv";


const validGroups = ["Looking for Love", "Free tonight?", "Coffee Date", "Let's be friend", "Like to go drinking", "Movie Lovers", "Creative Lovers", "Love Sports"]

const recommendationController = {
    // POST /api/recommendation/join?group=
    join: async (req: Request, res: Response) => {
        try {
            const username: string = req.body.username;
            if (!req.query.group || typeof req.query.group !== "string" || !validGroups.includes(req.query.group)) {
                res.status(400).json({ message: "Bad request" });
                return;
            }
            const group: string = req.query.group;


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
    //response: {profiles: Profile[]}
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

            const cur_pref = await db.preference.findUnique({ where: { username: req.body.username } });
            const cur_prof = await db.profile.findUnique({ where: { username: req.body.username } });

            if (!cur_prof || !cur_pref) {
                res.status(404).json({ message: "User requesting not found" });
                return;
            }

            if (req.query.group && !cur_prof.groups.includes(req.query.group)) {
                res.status(403).json({ message: "You must join the group first" });
                return
            }

            // Preferences enforcement (haven't excluded matched users yet)
            const users = await db.profile.findMany({
                where: {
                    username: {
                        not: cur_prof.username
                    },
                    account: {
                        role: Role.USER
                    }
                }
            });

            const recs: Profile[] = [];

            for (const user of users) {
                const age = getAge(user.birthDate);
                if (age > cur_pref.ageMax || age < cur_pref.ageMin) continue;
                if (cur_pref.showMe != user.identity && cur_pref.showMe != "Both") continue;
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

            if (recs.length == 0) {
                res.status(200).json({ profiles: [] });
                return;
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
                    where: { username: req.body.username },
                    data: {
                        recPage: 1
                    }
                });
            } else {
                await db.preference.update({
                    where: { username: req.body.username },
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

    // GET /api/recommendation/join?group=
    // response: {joined: Boolean}
    joinedYet: async (req: Request, res: Response) => {
        try {
            if (!req.query.group || typeof req.query.group !== "string") {
                res.status(400).json({ message: "Bad request" });
                return;
            }
            const prof = await db.profile.findUniqueOrThrow({ where: { username: req.body.username } });
            const groups = prof.groups;
            res.status(200).json({ joined: groups.includes(req.query.group) });
        } catch (error) {
            console.log(error);
            res.status(500).json({ message: "Something went wrong" });
        }
    },
};

export default recommendationController;
