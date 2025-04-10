const express = require('express');
const router = express.Router();
const { auth, isAdmin } = require('../middleware/auth');
const {
  createSubject,
  getSubjects,
  getSubject,
  updateSubject,
  deleteSubject
} = require('../controllers/subjectController');

// Admin routes
router.post('/', auth, isAdmin, createSubject);
router.get('/', auth, isAdmin, getSubjects);
router.get('/:id', auth, isAdmin, getSubject);
router.put('/:id', auth, isAdmin, updateSubject);
router.delete('/:id', auth, isAdmin, deleteSubject);

module.exports = router; 