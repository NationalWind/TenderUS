import express from "express";
import adminController from "../controllers/adminController";

const adminRouter = express.Router();

adminRouter.get("/report", adminController.getReportList);
adminRouter.get("/report/:id", adminController.getReportDetail);
adminRouter.post("/report", adminController.postReport);
adminRouter.post("/report/:id", adminController.postReportAction);
adminRouter.get("/account", adminController.getAccountList);
adminRouter.get("/account/:id", adminController.getAccountDetail);
adminRouter.post("/account/:id", adminController.postAccountAction);
adminRouter.get("/statistics", adminController.getStatistics);
adminRouter.get("/export", adminController.getExportStatistic);

export default adminRouter;
