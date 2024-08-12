import { PrismaClientKnownRequestError } from "@prisma/client/runtime/library";
import db from "../lib/db";
import { Request, Response } from "express";
import { PenaltyType } from "@prisma/client";

const adminController = {
  // GET /api/admin/report
  getReportList: async (_req: Request, res: Response) => {
    try {
      const reportList = await db.report.findMany({
        include: {
          reporter: { select: { username: true, Profile: { select: { avatarIcon: true } } } },
          reported: { select: { username: true, Profile: { select: { avatarIcon: true } } } },
        },
      });
      res.status(200).json(
        reportList.map((report) => ({
          ...report,
          reporter: report.reporter.username,
          reporterAvatar: report.reporter.Profile?.avatarIcon,
          reported: report.reported.username,
          reportedAvatar: report.reported.Profile?.avatarIcon,
        }))
      );
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: error.message });
    }
  },
  // GET /api/admin/report/:id
  getReportDetail: async (req: Request, res: Response) => {
    try {
      const { id } = req.params;
      const report = await db.report.findUnique({ where: { id } });
      res.status(200).json(report);
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: error.message });
    }
  },
  // POST /api/admin/report/:id
  postReportAction: async (req: Request, res: Response) => {
    try {
      const { id } = req.params;
      const report = await db.report.findUnique({ where: { id } });
      if (report) {
        const { type, date } = req.body;
        if (Object.keys(PenaltyType).includes(type)) {
          const toDate = new Date(date);
          if (!isNaN(toDate.getTime())) {
            // const { id, reported } = report;
            // await db.penalty.create({
            //   data: { type, toDate, accountId: reported },
            // });
            // await db.report.update({ where: { id }, data: { status: "REVIEWED" } });
            // res.status(200).json({ message: "Apply penalty successfully" });
          } else {
            res.status(400).json({ message: "Invalid date" });
          }
        } else {
          res.status(400).json({ message: "Invalid penalty type" });
        }
      } else {
        res.status(404).json({ message: "Report not found" });
      }
    } catch (error) {
      if (error instanceof PrismaClientKnownRequestError) {
        if (error.code === "P2023") res.status(400).json({ message: "Invalid ID" });
      } else {
        console.log(error);
        res.status(500).json({ message: error.message });
      }
    }
  },
  getAccountList: async (req: Request, res: Response) => {},
  getAccountDetail: async (req: Request, res: Response) => {},
  getAccountStatistics: async (req: Request, res: Response) => {},
};

export default adminController;
