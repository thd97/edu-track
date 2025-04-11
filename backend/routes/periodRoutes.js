const express = require('express');
const router = express.Router();
const { auth, isAdmin } = require('../middleware/auth');
const periodController = require('../controllers/periodController');

// Admin routes
router.post('/', auth, isAdmin, periodController.createPeriod);
router.get('/', auth, isAdmin, periodController.getPeriods);
router.get('/:id', auth, isAdmin, periodController.getPeriod);
router.put('/:id', auth, isAdmin, periodController.updatePeriod);
router.delete('/:id', auth, isAdmin, periodController.deletePeriod);

module.exports = router; 