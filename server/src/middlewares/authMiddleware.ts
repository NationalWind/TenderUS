import { NextFunction, Request, Response } from "express";

const authMiddeware = {
  checkIfRightUser: (req: Request, res: Response, next: NextFunction) => {
    const validation = "do something";
    if (validation) {
      next();
    } else {
      res.status(403).json("No permission");
    }
  },
};

export default authMiddeware;
