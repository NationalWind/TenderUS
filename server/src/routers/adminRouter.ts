import express from "express";
import adminController from "../controllers/adminController";

const adminRouter = express.Router();

adminRouter.get("/report", adminController.getReportList);
adminRouter.get("/report/:id", adminController.getReportDetail);
adminRouter.post("/report/:id", adminController.postReportAction);
adminRouter.get("/account", adminController.getAccountList);
adminRouter.get("/account/:id", adminController.getAccountDetail);
adminRouter.get("/account/:id/statistics", adminController.getAccountStatistics);

export default adminRouter;
