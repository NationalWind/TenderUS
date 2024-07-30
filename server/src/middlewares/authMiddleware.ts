import { NextFunction, Request, Response } from "express";
import jwt from "jsonwebtoken";
import { Role } from "@prisma/client";

const authMiddeware = {
  // Look up routers, if all requests using this middleware, later use req.body.id as username
  checkUser: (req: Request, res: Response, next: NextFunction) => {
    try {
      // "For now", a token is converted to a username, a FCMRegToken, a role
      const { token } = req.body;
      const decoded = jwt.verify(token, process.env.JWT_KEY as string) as { username: string, role: Role, FCMRegToken: string };
      req.body.id = decoded.username;
      req.body.role = decoded.role;
      req.body.FCMRegToken = decoded.FCMRegToken;
      delete req.body.token;
      next();
    } catch {
      res.status(500).json({ message: "Invalid token" });
    }
  },
};

export default authMiddeware;
