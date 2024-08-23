import { Request, Response } from "express";
import jwt from "jsonwebtoken";
import db from "../lib/db";
import { Account, Role, Like, Match, Profile, Preference } from "@prisma/client";
import { firebaseFCM } from "../lib/firebase";



const profileController = {
    //GET api/profile/prof
    //response: {prof: Profile}
    getProf: async (req: Request, res: Response) => {
        try {
            const prof = await db.profile.findUniqueOrThrow({ where: { username: req.body.username } });
            res.status(200).json(prof);
        } catch (error) {
            console.log(error);
            res.status(500).json({ message: "Something went wrong" });
        }
    },
    //GET api/profile/pref
    //response: {pref: Preference}
    getPref: async (req: Request, res: Response) => {
        try {
            const pref = await db.preference.findUniqueOrThrow({ where: { username: req.body.username } });
            res.status(200).json(pref);
        } catch (error) {
            console.log(error);
            res.status(500).json({ message: "Something went wrong" });
        }
    },
    // POST /api/profile/...
    createProf: async (req: Request, res: Response) => {
        try {
            const data: Profile = req.body;
            console.log(req.body)
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
