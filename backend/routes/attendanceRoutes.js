const express = require("express");
const router = express.Router();
const { auth, isAdmin, isTeacher } = require("../middleware/auth");
const attendanceController = require("../controllers/attendanceController");

// Admin routes - có thể thao tác với tất cả attendance records
router.post("/", auth, isAdmin, attendanceController.createAttendance);
router.get("/", auth, isAdmin, attendanceController.getAllAttendances);
router.get("/:id", auth, isAdmin, attendanceController.getAttendanceById);
router.put("/:id", auth, isAdmin, attendanceController.updateAttendance);
router.delete("/:id", auth, isAdmin, attendanceController.deleteAttendance);

// Teacher routes - chỉ có thể thao tác với attendance records của học sinh trong lớp mình
router.post("/teacher", auth, isTeacher, attendanceController.createAttendance);
router.get("/teacher/class/:classId", auth, isTeacher, attendanceController.getAttendancesByClass);
router.get("/teacher/class/:classId/date/:date", auth, isTeacher, attendanceController.getAttendancesByClassAndDate);
router.put("/teacher/:id", auth, isTeacher, attendanceController.updateAttendance);

module.exports = router; 