import { Request, Response } from "express";
import jwt from "jsonwebtoken";
import db from "../lib/db";
import { Account, Role, Like, Match, Profile } from "@prisma/client";
import { firebaseFCM } from "../lib/firebase";


const MatchFCM = async (username: string) => {
    const foundAccount = await db.account.findUnique({ where: { username: username } });

    if (foundAccount!.FCMRegToken === null) return;

    const registrationToken = foundAccount!.FCMRegToken;

    await firebaseFCM.sendFCM(registrationToken, "You have a new match!");
};

const matchPollers: { [key: string]: { res: Response, timeout: NodeJS.Timeout } } = {};


const swipeController = {
    // POST /like {likedUsername: String}
    // response: {match: Boolean}
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


            await db.like.upsert({
                where: {
                    username_likedUsername: {
                        username: data.username,
                        likedUsername: data.likedUsername
                    }
                },
                create: data,
                update: {}
            });
            const checked = await db.like.findFirst({ where: { username: data.likedUsername, likedUsername: data.username } });
            var match = false;
            if (checked) {
                match = true;
                let user1 = data.username;
                let user2 = data.likedUsername;
                if (data.username > data.likedUsername) {
                    user1 = data.likedUsername;
                    user2 = data.username;
                }
                await db.match.upsert({
                    where: {
                        user1_user2: {
                            user1: user1,
                            user2: user2
                        }
                    },
                    update: {
                        createdAt: new Date()
                    },
                    create: {
                        user1: user1,
                        user2: user2,
                        createdAt: new Date()
                    },
                });
                /*await */MatchFCM(data.likedUsername);
                const user = [data.username, data.likedUsername];
                for (let i = 0; i < 2; i++) {
                    if (matchPollers[user[i]]) {
                        clearTimeout(matchPollers[user[i]].timeout);
                        try {
                            matchPollers[user[i]].res.status(200).json(
                                await db.profile.findUnique({
                                    where: {
                                        username: user[(i + 1) % 2]
                                    },
                                    select: {
                                        username: true,
                                        avatarIcon: true,
                                        displayName: true,
                                        isActive: true,
                                    }
                                })
                            );
                        } catch (error) {
                            console.log(error);
                        }
                        delete matchPollers[user[i]];
                    }
                }



            }

            res.status(200).json({ match });
        } catch (error) {
            console.log(error);
            res.status(500).json({ message: "Something went wrong" });
        }
    },

    // POST /pass {passedUsername: String}
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
                }, 3600000)
            };
        } catch (error) {
            res.status(500).json({ message: "Something went wrong" });
        }
    }
};

export default swipeController;
