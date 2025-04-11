const express = require("express");
const router = express.Router();
const { auth, isAdmin, isTeacherInClass } = require("../middleware/auth");
const studentController = require("../controllers/studentController");

// Admin only
router.post("/", auth, isAdmin, studentController.createStudent);
router.get("/", auth, isAdmin, studentController.getStudents);
router.get("/:id", auth, isAdmin, studentController.getStudent);
router.put("/:id", auth, isAdmin, studentController.updateStudent);
router.delete("/:id", auth, isAdmin, studentController.deleteStudent);
router.post("/filter", auth, isAdmin, studentController.filterStudents);

// Teacher routes
router.get("/class/:classId", auth, isTeacherInClass, studentController.getStudentsByClass);

module.exports = router;