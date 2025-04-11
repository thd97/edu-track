const express = require("express");
const router = express.Router();
const { auth, isAdmin } = require("../middleware/auth");
const subjectController = require("../controllers/subjectController");

// Admin only
router.post("/", auth, isAdmin, subjectController.createSubject);
router.get("/", auth, isAdmin, subjectController.getSubjects);
router.get("/:id", auth, isAdmin, subjectController.getSubject);
router.put("/:id", auth, isAdmin, subjectController.updateSubject);
router.delete("/:id", auth, isAdmin, subjectController.deleteSubject);

module.exports = router;
