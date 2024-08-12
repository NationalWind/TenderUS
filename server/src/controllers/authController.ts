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
      const { username } = req.body.username;
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
      const data: Omit<Account, "id"> & { token?: string } = req.body;

      if (!data.username || !data.password || !data.token) {
        res.status(400).json({ message: "Bad request" });
        return;
      }

      const decodedToken = await AdmGetAuth().verifyIdToken(data.token);
      const takenUsername = await db.account.count({ where: { username: data.username } });
      await AdmGetAuth().revokeRefreshTokens(decodedToken.uid);

      if (takenUsername) {
        res.status(409).json({ message: "This username has already taken" });
      } else {
        if (data.email) {
          if (decodedToken.email !== data.email || !decodedToken.email_verified) {
            res.status(403).json({ message: "No permission" });
            return;
          }
          await AdmGetAuth().updateUser(decodedToken.uid, { emailVerified: false });
        } else if (data.phone) {
          if (decodedToken.phone_number !== data.phone) {
            res.status(403).json({ message: "No permission" });
            return;
          }
        } else {
          res.status(400).json({ message: "Bad request" });
          return;
        }

        data.FirebaseUID = decodedToken.uid
        data.password = await bcrypt.hash(data.password, 10);
        data.role = Role.USER;
        delete data.token

        const account = await db.account.create({ data });
        res.status(200).json({ message: "OK" });
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

          res.status(200).json({ token, firebaseToken, firstTime });
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
      const data: Omit<Account, "id" | "role"> & { token?: string } = req.body;

      if (!data.username || !data.password || !data.token) {
        res.status(400).json({ message: "Bad request" });
        return;
      }

      const decodedToken = await AdmGetAuth().verifyIdToken(data.token);
      const foundAccount = await db.account.findUnique({ where: { username: data.username } });
      await AdmGetAuth().revokeRefreshTokens(decodedToken.uid);

      if (foundAccount) {
        if (data.email) {
          if (decodedToken.email !== data.email || !decodedToken.email_verified) {
            res.status(403).json({ message: "No permission" });
            return;
          }
          if (data.email !== foundAccount.email) {
            res.status(400).json({ message: "No email match with this username" });
            return;
          }
          await AdmGetAuth().updateUser(decodedToken.uid, { emailVerified: false });
        } else if (data.phone) {
          if (decodedToken.phone_number !== data.phone) {
            res.status(403).json({ message: "No permission" });
            return;
          }

          if (data.phone !== foundAccount.phone) {
            res.status(400).json({ message: "No phone number match with this username" });
            return;
          }
        } else {
          res.status(400).json({ message: "Bad request" });
          return;
        }

        const newPassword = await bcrypt.hash(data.password, 10);
        const account = await db.account.update({
          where: { username: data.username },
          data: { password: newPassword },
        });
        res.status(200).json({ message: "OK" });
      } else {
        res.json({ message: "Username not found" });
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
