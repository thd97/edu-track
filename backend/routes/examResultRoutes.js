const express = require("express");
const router = express.Router();
const { auth, isAdmin, isTeacher } = require("../middleware/auth");
const examResultController = require("../controllers/examResultController");

// Admin routes - có thể thao tác với tất cả exam results
router.post("/", auth, isAdmin, examResultController.createExamResult);
router.get("/", auth, isAdmin, examResultController.getAllExamResults);
router.get("/:id", auth, isAdmin, examResultController.getExamResultById);
router.put("/:id", auth, isAdmin, examResultController.updateExamResult);
router.delete("/:id", auth, isAdmin, examResultController.deleteExamResult);

// Teacher routes - chỉ có thể thao tác với exam results của học sinh trong lớp mình
router.post("/teacher", auth, isTeacher, examResultController.createExamResult);
router.get("/teacher/class/:classId", auth, isTeacher, examResultController.getExamResultsByClass);
router.get("/teacher/class/:classId/exam/:examId", auth, isTeacher, examResultController.getExamResultsByClassAndExam);
router.put("/teacher/:id", auth, isTeacher, examResultController.updateExamResult);

module.exports = router; 