const express = require('express');
const router = express.Router();
const { auth, isAdmin } = require('../middleware/auth');
const {
  createExam,
  getExams,
  getExam,
  updateExam,
  deleteExam
} = require('../controllers/examController');

// Admin routes
router.post('/', auth, isAdmin, createExam);
router.get('/', auth, isAdmin, getExams);
router.get('/:id', auth, isAdmin, getExam);
router.put('/:id', auth, isAdmin, updateExam);
router.delete('/:id', auth, isAdmin, deleteExam);

module.exports = router; 