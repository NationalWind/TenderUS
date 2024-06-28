import { NextFunction, Request, Response } from "express";
import jwt from "jsonwebtoken";

const authMiddeware = {
  checkUser: (req: Request, res: Response, next: NextFunction) => {
    try {
      const { id } = req.params;
      const { token } = req.body;
      const decoded = jwt.verify(token, process.env.JWT_KEY as string) as { id: string };
      if (decoded.id === id) {
        next();
      } else {
        res.status(403).json({ message: "No permission" });
      }
    } catch {
      res.status(500).json({ message: "Invalid token" });
    }
  },
};

export default authMiddeware;
