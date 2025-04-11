const express = require("express");
const router = express.Router();
const { auth, isAdmin } = require("../middleware/auth");
const classController = require("../controllers/classController");

// Admin only
router.post("/", auth, isAdmin, classController.createClass);
router.get("/", auth, isAdmin, classController.getClasses);
router.get("/:id", auth, isAdmin, classController.getClass);
router.put("/:id", auth, isAdmin, classController.updateClass);
router.delete("/:id", auth, isAdmin, classController.deleteClass);

module.exports = router;