import { NextFunction, Request, Response } from "express";
import jwt, { decode } from "jsonwebtoken";
import { Role } from "@prisma/client";

const authMiddeware = {
  // Look up routers, if all requests using this middleware, later use req.body.id as username
  checkUser: (req: Request, res: Response, next: NextFunction) => {
    try {
      // "For now", a token is converted to a username, a role
      const bearer = req.headers.authorization;
      if (!bearer) {
        res.status(401).json({ message: "Unauthorized" });
        return;
      }
      const token = bearer.substring(7);
      const decoded = jwt.verify(token, process.env.JWT_KEY as string) as { username: string, role: Role };
      req.body.id = decoded.username;
      req.body.role = decoded.role;
      delete req.body.token;
      next();
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: "Invalid token" });
    }
  },
};

export default authMiddeware;
