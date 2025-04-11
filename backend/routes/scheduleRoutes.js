const express = require("express");
const router = express.Router();
const { auth, isAdmin, isTeacher } = require("../middleware/auth");
const scheduleController = require("../controllers/scheduleController");

// Admin routes - có thể thao tác với tất cả thời khóa biểu
router.post("/", auth, isAdmin, scheduleController.createSchedule);
router.get("/", auth, isAdmin, scheduleController.getAllSchedules);
router.get("/:id", auth, isAdmin, scheduleController.getScheduleById);
router.put("/:id", auth, isAdmin, scheduleController.updateSchedule);
router.delete("/:id", auth, isAdmin, scheduleController.deleteSchedule);

// Teacher routes - chỉ có thể xem thời khóa biểu của mình
router.get("/teacher/:teacherId", auth, isTeacher, scheduleController.getSchedulesByTeacher);

// Public routes - có thể xem thời khóa biểu của lớp
router.get("/class/:classId", scheduleController.getSchedulesByClass);

module.exports = router; 