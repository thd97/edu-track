const express = require("express");
const router = express.Router();
const { auth, isAdmin, isTeacherOfClass } = require("../middleware/auth");
const {
  createStudent,
  getStudents,
  getStudent,
  updateStudent,
  deleteStudent,
  getStudentsByClass,
  filterStudents,
} = require("../controllers/studentController");

// Admin routes
router.post("/", auth, isAdmin, createStudent);
router.get("/", auth, isAdmin, getStudents);
router.get("/:id", auth, isAdmin, getStudent);
router.put("/:id", auth, isAdmin, updateStudent);
router.delete("/:id", auth, isAdmin, deleteStudent);
router.post("/filter", auth, isAdmin, filterStudents);

// Teacher routes
router.get("/class/:classId", auth, isTeacherOfClass, getStudentsByClass);

module.exports = router; 