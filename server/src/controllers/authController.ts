import { Request, Response } from "express";

const authController = {
  getInfo: async (req: Request, res: Response) => {
    res.status(200).json("Path: /api/auth/getInfo. This is temporary response, implement later");
  },
  register: async (req: Request, res: Response) => {
    res.status(200).json("Path: /api/auth/register. This is temporary response, implement later");
  },
  login: async (req: Request, res: Response) => {
    res.status(200).json("Path: /api/auth/login. This is temporary response, implement later");
  },
};

export default authController;
