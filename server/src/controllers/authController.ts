import bcrypt from "bcrypt";
import { Request, Response } from "express";
import jwt from "jsonwebtoken";
import db from "../lib/db";
import { Account, Role } from "@prisma/client";
import { getAuth } from "../lib/firebase";

const authController = {
  // GET /api/auth/accout/:id
  getAccount: async (req: Request, res: Response) => {
    try {
      const { id } = req.params;
      const account = await db.account.findUnique({ where: { id } });
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

      if (!data.token) {
        res.status(400).json({ message: "Bad request" });
        return;
      }

      const takenUsername = await db.account.count({ where: { username: data.username } });
      if (takenUsername) {
        res.status(409).json({ message: "This username has already taken" });
      } else {
        // TODO: email or phone validation
        const decodedToken = await getAuth().verifyIdToken(data.token);
        if (data.email) {
          if (decodedToken.email !== data.email) {
            res.status(403).json({ message: "No permission" });
            return;
          }
        } else if (data.phone) {
          if (decodedToken.phone_number !== data.phone) {
            res.status(403).json({ message: "No permission" });
            return;
          }
        } else {
          res.status(400).json({ message: "Bad request" });
          return;
        }

        data.password = await bcrypt.hash(data.password, 10);
        data.role = Role.USER;
        delete data.token

        const account = await db.account.create({ data });
        res.status(200).json(account);
      }
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },
  // POST /api/auth/login
  login: async (req: Request, res: Response) => {
    try {
      const data: { username: string; password: string } = req.body;
      const foundAccount = await db.account.findUnique({ where: { username: data.username } });
      if (foundAccount) {
        const isMatched = await bcrypt.compare(data.password, foundAccount.password);
        if (isMatched) {
          const token = jwt.sign(foundAccount, process.env.JWT_KEY as string);
          res.status(200).json({ token });
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
  // POST /api/auth/changePassword
  changePassword: async (req: Request, res: Response) => {
    try {
      const data: { username: string; password: string; newPassword: string } = req.body;
      const foundAccount = await db.account.findUnique({ where: { username: data.username } });
      if (foundAccount) {
        const isMatched = await bcrypt.compare(data.password, foundAccount.password);
        if (isMatched) {
          const newPassword = await bcrypt.hash(data.newPassword, 10);
          const account = await db.account.update({
            where: { username: data.username },
            data: { password: newPassword },
          });
          res.status(200).json(account);
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
      const data: Omit<Account, "id" | "role"> = req.body;
      const foundAccount = await db.account.findUnique({ where: { username: data.username } });
      if (foundAccount) {
        if (data.email) {
          // TODO: email validation
          if (data.email !== foundAccount.email) {
            res.status(400).json({ message: "No email match with this username" });
            return;
          }
        } else if (data.phone) {
          // TODO: phone validation
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
        res.status(200).json(account);
      } else {
        res.json({ message: "Username not found" });
      }
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Something went wrong" });
    }
  },
};

export default authController;
