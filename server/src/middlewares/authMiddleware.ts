import { NextFunction, Request, Response } from "express";
import jwt, { decode } from "jsonwebtoken";
import { Role } from "@prisma/client";

const authMiddeware = {
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
            req.body.username = decoded.username;
            req.body.role = decoded.role;

            if (req.body.role === "GUEST") {
                res.status(403).json({ message: "Forbidden" });
                return;
            }

            delete req.body.token;
            next();
        } catch (error) {
            console.log(error);
            res.status(500).json({ message: "Invalid token" });
        }
    },
};

export default authMiddeware;
