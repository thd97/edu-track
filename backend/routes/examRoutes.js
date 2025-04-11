const express = require('express');
const router = express.Router();
const { auth, isAdmin } = require('../middleware/auth');
const examController = require('../controllers/examController');

// Admin routes
router.post('/', auth, isAdmin, examController.createExam);
router.get('/', auth, isAdmin, examController.getExams);
router.get('/:id', auth, isAdmin, examController.getExam);
router.put('/:id', auth, isAdmin, examController.updateExam);
router.delete('/:id', auth, isAdmin, examController.deleteExam);

module.exports = router; 