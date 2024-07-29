import { NextFunction, Request, Response } from "express";
import jwt from "jsonwebtoken";

const authMiddeware = {
  // Look up routers, if all requests using this middleware, later use req.body.id as username
  checkUser: (req: Request, res: Response, next: NextFunction) => {
    try {
      const { token } = req.body;
      const decoded = jwt.verify(token, process.env.JWT_KEY as string) as { username: string };
      req.body.id = decoded.username;
      delete req.body.token;
      next();
    } catch {
      res.status(500).json({ message: "Invalid token" });
    }
  },
};

export default authMiddeware;
