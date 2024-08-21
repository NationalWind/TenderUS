import bcrypt from "bcrypt";
import { Request, Response } from "express";
import jwt from "jsonwebtoken";
import db from "../lib/db";
import { Account, Role } from "@prisma/client";
import { AdmGetAuth } from "../lib/firebase";


const authController = {
    // GET /api/auth/account
    getAccount: async (req: Request, res: Response) => {
        try {
            const { username } = req.body;
            const account = await db.account.findUnique({ where: { username } });
            res.status(200).json(account);
        } catch (error) {
            console.log(error);
            res.status(500).json({ message: "Something went wrong" });
        }
    },

    // POST /api/auth/register
    register: async (req: Request, res: Response) => {
        try {
            const request: { username: string, password: string, token: string } = req.body;

            if (!request.username || !request.password || !request.token) {
                res.status(400).json({ message: "Bad request" });
                return;
            }

            const decodedToken = await AdmGetAuth().verifyIdToken(request.token);
            await AdmGetAuth().revokeRefreshTokens(decodedToken.uid);

            if (decodedToken.email) {
                if (!decodedToken.email_verified) {
                    res.status(403).json({ message: "No permission" });
                    return;
                }
                const check = await db.account.findFirst({
                    where: {
                        email: decodedToken.email
                    }
                });
                if (check) {
                    res.status(403).json({ message: "The email address has already been registered" })
                    return;
                }
                await AdmGetAuth().updateUser(decodedToken.uid, { emailVerified: false });
                const data: Omit<Account, "id" | "phone" | "FCMRegToken"> = {
                    FirebaseUID: decodedToken.uid,
                    username: request.username,
                    email: decodedToken.email,
                    password: await bcrypt.hash(request.password, 10),
                    role: Role.USER,
                }

                await db.account.create({ data });
                res.status(200).json({ message: "OK" });
            } else if (decodedToken.phone_number) {
                const check = await db.account.findFirst({
                    where: {
                        phone: decodedToken.phone_number
                    }
                });
                if (check) {
                    res.status(403).json({ message: "The phone number has already been registered" })
                    return;
                }

                const data: Omit<Account, "id" | "email" | "FCMRegToken"> = {
                    FirebaseUID: decodedToken.uid,
                    username: request.username,
                    phone: decodedToken.phone_number,
                    password: await bcrypt.hash(request.password, 10),
                    role: Role.USER,
                }

                await db.account.create({ data });
            } else {
                res.status(403).json({ message: "Invalid token" });
                return;
            }



        } catch (error) {
            console.log(error);
            res.status(500).json({ message: "Something went wrong" });
        }
    },

    // POST /api/auth/login
    login: async (req: Request, res: Response) => {
        try {
            interface LoginRequest {
                username: string;
                password: string;
                FCMRegToken: string;
            }
            const data: LoginRequest = {
                username: req.body.username,
                password: req.body.password,
                FCMRegToken: req.body.FCMRegToken,
            }
            if (!data.username || !data.password || !data.FCMRegToken) {
                res.status(400).json({ message: "Bad request" });
                return;
            }

            const foundAccount = await db.account.findUnique({ where: { username: data.username } });
            if (foundAccount) {
                await db.account.update({ where: { username: data.username }, data: { FCMRegToken: data.FCMRegToken } });
                const isMatched = await bcrypt.compare(data.password, foundAccount.password);
                if (isMatched) {
                    const token = jwt.sign(foundAccount, process.env.JWT_KEY as string);

                    const firebaseToken = await AdmGetAuth().createCustomToken(foundAccount.FirebaseUID);

                    const firstTime = await db.profile.findUnique({ where: { username: data.username } }) == null;

                    res.status(200).json({ token, firebaseToken, firstTime, role: foundAccount.role });
                } else {
                    res.status(401).json({ message: "Wrong password" });
                }
            } else {
                res.status(404).json({ message: "Username not found" });
            }
        } catch (error) {
            console.log(error);
            res.status(500).json({ message: "Something went wrong" });
        }
    },

    // POST /api/auth/resetPassword
    resetPassword: async (req: Request, res: Response) => {
        try {
            const data: { password: string, token: string } = req.body

            if (!data.password || !data.token) {
                res.status(400).json({ message: "Bad request" });
                return;
            }

            const decodedToken = await AdmGetAuth().verifyIdToken(data.token);
            await AdmGetAuth().revokeRefreshTokens(decodedToken.uid);

            if (decodedToken.email) {
                if (!decodedToken.email_verified) {
                    res.status(403).json({ message: "No permission" });
                    return;
                }
                await AdmGetAuth().updateUser(decodedToken.uid, { emailVerified: false });
                const newPassword = await bcrypt.hash(data.password, 10);
                await db.account.updateMany({
                    where: { email: decodedToken.email },
                    data: { password: newPassword },
                });
                res.status(200).json({ message: "OK" });
            } else if (decodedToken.phone_number) {
                const newPassword = await bcrypt.hash(data.password, 10);
                await db.account.updateMany({
                    where: { phone: decodedToken.phone_number },
                    data: { password: newPassword },
                });
                res.status(200).json({ message: "OK" });
            } else {
                res.status(403).json({ message: "Invalid token" });
            }



        } catch (error) {
            console.log(error);
            res.status(500).json({ message: "Something went wrong" });
        }
    },

    // POST /api/auth/signOut
    signOut: async (req: Request, res: Response) => {
        try {
            const username = req.body.username;
            await db.account.update({ where: { username: username }, data: { FCMRegToken: null } });
            res.status(200).json({ message: "OK" });
        } catch (error) {
            console.log(error);
            res.status(500).json({ message: "Something went wrong" });
        }
    },

    // POST /api/auth/changePassword
    // changePassword: async (req: Request, res: Response) => {
    //   try {
    //     const data: { username: string; password: string; newPassword: string } = req.body;
    //     const foundAccount = await db.account.findUnique({ where: { username: data.username } });
    //     if (foundAccount) {
    //       const isMatched = await bcrypt.compare(data.password, foundAccount.password);
    //       if (isMatched) {
    //         const newPassword = await bcrypt.hash(data.newPassword, 10);
    //         const account = await db.account.update({
    //           where: { username: data.username },
    //           data: { password: newPassword },
    //         });
    //         res.status(200).json(account);
    //       } else {
    //         res.status(401).json({ message: "Wrong password" });
    //       }
    //     } else {
    //       res.status(404).json({ message: "Username not found" });
    //     }
    //   } catch (error) {
    //     console.log(error);
    //     res.status(500).json({ message: "Something went wrong" });
    //   }
    // },
};

export default authController;
