import { PrismaClientKnownRequestError } from "@prisma/client/runtime/library";
import db from "../lib/db";
import { Request, Response } from "express";

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
      const report = await db.report.findUnique({
        where: { id },
        include: {
          reporter: { select: { username: true, Profile: { select: { avatarIcon: true } } } },
          reported: { select: { username: true, Profile: { select: { avatarIcon: true } } } },
        }
      });
      if (report) {
        res.status(200).json({
          ...report,
          reporter: report.reporter.username,
          reporterAvatar: report.reporter.Profile?.avatarIcon,
          reported: report.reported.username,
          reportedAvatar: report.reported.Profile?.avatarIcon,
        });
      } else {
        res.status(404).json({ message: "Report not found" })
      }
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: error.message });
    }
  },
  // POST /api/admin/report/:id
  postReportAction: async (req: Request, res: Response) => {
    console.log(req.body)
    try {
      const { id } = req.params;
      const report = await db.report.findUnique({ where: { id } });
      if (report) {
        const { penalty, deleteContent } = req.body;
        if (["NONE", "RESTRICT_DISCOVER", "RESTRICT_MESSAGE", "RESTRICT_BOTH", "BAN"].includes(penalty)) {
          const { id, reportedId } = report;
          // Restrict
          if (penalty === "RESTRICT_BOTH") {
            await db.penalty.create({ data: { type: "RESTRICT_DISCOVER", accountId: reportedId } });
            await db.penalty.create({ data: { type: "RESTRICT_MESSAGE", accountId: reportedId } });
          } else if (penalty !== "NONE") {
            await db.penalty.create({ data: { type: penalty, accountId: reportedId } });
          }
          // Delete content
          if (deleteContent) {
            // TODO: delete content, implement when client report done
          }
          // Update status
          await db.report.update({ where: { id }, data: { status: "REVIEWED" } });
          res.status(200).json({ message: "Success" })
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
  // GET /api/admin/account
  getAccountList: async (_req: Request, res: Response) => {
    try {
      const accountList = await db.account.findMany({ include: { Profile: { select: { avatarIcon: true } } } })
      res.status(200).json(
        accountList.map((account) => ({
          ...account,
          avatar: account.Profile?.avatarIcon,
        }))
      );
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: error.message });
    }
  },
  getAccountDetail: async (req: Request, res: Response) => {
    try {
      const { id } = req.params;
      const account = await db.account.findUnique({
        where: { id },
        include: { Profile: { select: { avatarIcon: true } }, penalty: { select: { type: true } } }
      })
      if (account) {
        res.status(200).json(
          {
            ...account,
            avatar: account.Profile?.avatarIcon,
            penalty: account.penalty.map((penalty) => penalty.type)
          }
        );
      } else {
        res.status(404).json({ message: "Account not found" })
      }
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: error.message });
    }
  },
  getAccountStatistics: async (req: Request, res: Response) => { },
};

export default adminController;
