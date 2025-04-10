const express = require('express');
const router = express.Router();
const { auth, isAdmin } = require('../middleware/auth');
const {
  createPeriod,
  getPeriods,
  getPeriod,
  updatePeriod,
  deletePeriod
} = require('../controllers/periodController');

// Admin routes
router.post('/', auth, isAdmin, createPeriod);
router.get('/', auth, isAdmin, getPeriods);
router.get('/:id', auth, isAdmin, getPeriod);
router.put('/:id', auth, isAdmin, updatePeriod);
router.delete('/:id', auth, isAdmin, deletePeriod);

module.exports = router; 