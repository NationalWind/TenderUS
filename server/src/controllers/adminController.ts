import { PrismaClientKnownRequestError } from "@prisma/client/runtime/library";
import db from "../lib/db";
import { Request, Response } from "express";
import QuickChart from "quickchart-js";
import { Account, Event } from "@prisma/client";
import jwt from "jsonwebtoken";
import axios from "axios";
import PDFDocument from "pdfkit";

const adminController = {
  // GET /api/admin/report
  getReportList: async (_req: Request, res: Response) => {
    try {
      const reportList = await db.report.findMany({
        include: {
          reporter: {
            select: {
              username: true,
              Profile: { select: { avatarIcon: true } },
            },
          },
          reported: {
            select: {
              username: true,
              Profile: { select: { avatarIcon: true } },
            },
          },
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
  // This should be in a new controller but im too lazy
  // POST /api/admin/report
  postReport: async (req: Request, res: Response) => {
    try {
      const { reporter, reported, message } = req.body as {
        [key: string]: string;
      };
      const decoded = jwt.decode(reporter) as Account;
      const reportedAccount = await db.account.findUnique({
        where: { username: reported },
      });
      if (!reportedAccount) {
        res.status(404).json({ message: "Reported account id not found" });
        return;
      }
      await db.report.create({
        data: {
          reporterId: decoded.id,
          reportedId: reportedAccount.id,
          message: message.trim(),
        },
      });
      res.status(200).json({ message: "Success" });
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
          reporter: {
            select: {
              username: true,
              Profile: { select: { avatarIcon: true } },
            },
          },
          reported: {
            select: {
              username: true,
              Profile: { select: { avatarIcon: true } },
            },
          },
        },
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
        res.status(404).json({ message: "Report not found" });
      }
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: error.message });
    }
  },
  // POST /api/admin/report/:id
  postReportAction: async (req: Request, res: Response) => {
    console.log(req.body);
    try {
      const { id } = req.params;
      const report = await db.report.findUnique({ where: { id } });
      if (report) {
        const { penalty, deleteContent } = req.body;
        if (
          [
            "NONE",
            "RESTRICT_DISCOVER",
            "RESTRICT_MESSAGE",
            "RESTRICT_BOTH",
            "BAN",
          ].includes(penalty)
        ) {
          const { id, reportedId } = report;
          // Restrict
          if (penalty === "RESTRICT_BOTH") {
            await db.penalty.create({
              data: { type: "RESTRICT_DISCOVER", accountId: reportedId },
            });
            await db.penalty.create({
              data: { type: "RESTRICT_MESSAGE", accountId: reportedId },
            });
          } else if (penalty !== "NONE") {
            await db.penalty.create({
              data: { type: penalty, accountId: reportedId },
            });
          }
          // Delete content
          if (deleteContent) {
            // TODO: delete content, implement when client report done
          }
          // Update status
          await db.report.update({
            where: { id },
            data: { status: "REVIEWED" },
          });
          res.status(200).json({ message: "Success" });
        } else {
          res.status(400).json({ message: "Invalid penalty type" });
        }
      } else {
        res.status(404).json({ message: "Report not found" });
      }
    } catch (error) {
      if (error instanceof PrismaClientKnownRequestError) {
        if (error.code === "P2023")
          res.status(400).json({ message: "Invalid ID" });
      } else {
        console.log(error);
        res.status(500).json({ message: error.message });
      }
    }
  },
  // GET /api/admin/account
  getAccountList: async (_req: Request, res: Response) => {
    try {
      const accountList = await db.account.findMany({
        include: { Profile: { select: { avatarIcon: true } } },
      });
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
  // GET /api/admin/account/:id
  getAccountDetail: async (req: Request, res: Response) => {
    try {
      const { id } = req.params;
      const account = await db.account.findUnique({
        where: { id },
        include: {
          Profile: { select: { avatarIcon: true } },
          penalty: { select: { id: true, type: true } },
        },
      });
      if (account) {
        res.status(200).json({
          ...account,
          avatar: account.Profile?.avatarIcon,
        });
      } else {
        res.status(404).json({ message: "Account not found" });
      }
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: error.message });
    }
  },
  // POST /api/admin/account/:id
  postAccountAction: async (req: Request, res: Response) => {
    try {
      const { penaltyDeleted } = req.body;
      for (let penalty of penaltyDeleted) {
        await db.penalty.delete({ where: { id: penalty.id } });
      }
      res.status(200).json({ message: "Success" });
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: error.message });
    }
  },
  getStatistics: async (req: Request, res: Response) => {
    try {
      const { duration, event, accountId } = req.query as {
        [key: string]: string;
      };

      if (!["daily", "monthly", "yearly"].includes(duration)) {
        res.status(400).json({ message: "Invalid duration" });
        return;
      }
      if (!Object.keys(Event).includes(event)) {
        res.status(400).json({ message: "Invalid event type" });
        return;
      }

      const dates = [];
      if (duration == "daily") {
        const year = new Date().getFullYear();
        const month = new Date().getMonth();
        const daysInMonth = new Date(year, month + 1, 0).getDate();
        for (let day = 1; day <= daysInMonth; day++) {
          dates.push(new Date(year, month, day, 0, 0, 0));
        }
      } else if (duration == "monthly") {
        const year = new Date().getFullYear();
        for (let month = 0; month < 12; month++) {
          dates.push(new Date(year, month, 1, 0, 0, 0));
        }
      } else {
        const currentYear = new Date().getFullYear();
        const startYear = Math.floor(currentYear / 10) * 10;
        for (let year = 1; year <= 10; year++) {
          dates.push(new Date(startYear + year, 0, 1, 0, 0, 0));
        }
      }

      const title = `${duration} ${event.toLowerCase().replace("_", " ")}`;
      const labels = [];
      const data = [];
      for (let date of dates) {
        let toDate;
        if (duration == "daily") {
          labels.push(date.toLocaleString("default", { day: "2-digit" }));
          toDate = new Date(
            date.getFullYear(),
            date.getMonth(),
            date.getDate() + 1
          );
        } else if (duration == "monthly") {
          labels.push(date.toLocaleString("default", { month: "long" }));
          toDate = new Date(date.getFullYear(), date.getMonth() + 1, 0);
        } else {
          labels.push(date.getFullYear());
          toDate = new Date(date.getFullYear() + 1, 0, 0);
        }
        data.push(
          await db.history.count({
            where: {
              event: event as Event,
              accountId,
              time: {
                gte: date,
                lt: toDate,
              },
            },
          })
        );
      }

      const myChart = new QuickChart();
      myChart.setConfig({
        type: "bar",
        data: {
          labels,
          datasets: [
            {
              label: title,
              backgroundColor: "#BD0D36",
              data,
            },
          ],
        },
      });

      // res.send(`<img src="${myChart.getUrl()}" />`);
      res.redirect(myChart.getUrl());
    } catch (error) {
      if (error instanceof PrismaClientKnownRequestError) {
        if (error.code === "P2023")
          res.status(400).json({ message: "Invalid ID" });
      } else {
        console.log(error);
        res.status(500).json({ message: error.message });
      }
    }
  },
  getExportStatistic: async (_req: Request, res: Response) => {
    try {
      const urls = [
        "http://localhost:8001/api/admin/statistics?duration=daily&event=ACCOUNT_CREATED",
        "http://localhost:8001/api/admin/statistics?duration=monthly&event=ACCOUNT_CREATED",
        "http://localhost:8001/api/admin/statistics?duration=yearly&event=ACCOUNT_CREATED",
        "http://localhost:8001/api/admin/statistics?duration=daily&event=ACCOUNT_ONLINE",
        "http://localhost:8001/api/admin/statistics?duration=monthly&event=ACCOUNT_ONLINE",
        "http://localhost:8001/api/admin/statistics?duration=yearly&event=ACCOUNT_ONLINE",
      ];

      const doc = new PDFDocument({ bufferPages: true });

      for (let i = 0; i < urls.length; i++) {
        const image = await axios({
          url: urls[i],
          responseType: "arraybuffer",
        });
        const width = doc.page.width - 100;
        const height = (600 * (doc.page.width - 100)) / 1000;
        doc.image(image.data, 50, (height + 50) * (i % 2) + 50, {
          width,
          height,
        });
        if (i % 2 && i !== urls.length - 1) doc.addPage();
      }
      doc.end();

      res.setHeader("Content-Type", "application/pdf");
      doc.pipe(res);
    } catch (error) {
      console.log(error);
      res.status(500).json({ message: error.message });
    }
  },
};

export default adminController;
