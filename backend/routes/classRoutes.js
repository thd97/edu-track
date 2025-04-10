const express = require("express");
const router = express.Router();
const { auth, isAdmin } = require("../middleware/auth");
const {
  createClass,
  getClasses,
  getClass,
  updateClass,
  deleteClass,
} = require("../controllers/classController");

// Admin routes
router.post("/", auth, isAdmin, createClass);
router.get("/", auth, isAdmin, getClasses);
router.get("/:id", auth, isAdmin, getClass);
router.put("/:id", auth, isAdmin, updateClass);
router.delete("/:id", auth, isAdmin, deleteClass);

module.exports = router; 