const Period = require('../models/Period');

// Create new period
const createPeriod = async (req, res) => {
  try {
    const { periodNumber, startTime, endTime } = req.body;

    const newPeriod = await Period.create({
      periodNumber,
      startTime,
      endTime
    });

    res.status(201).json({
      success: true,
      data: newPeriod
    });
  } catch (error) {
    console.error("Create period error:", error);
    if (error.code === 11000) {
      return res.status(400).json({
        success: false,
        message: "Period number already exists"
      });
    }
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

// Get all periods
const getPeriods = async (req, res) => {
  try {
    const { periodNumber, startTime, endTime, page = 1, limit = 10 } = req.query;
    
    // Build filter query
    const filter = {};
    
    if (periodNumber) {
      filter.periodNumber = parseInt(periodNumber);
    }
    
    if (startTime) {
      filter.startTime = { $regex: startTime, $options: 'i' };
    }
    
    if (endTime) {
      filter.endTime = { $regex: endTime, $options: 'i' };
    }

    const total = await Period.countDocuments(filter);
    const periods = await Period.find(filter)
      .skip((page - 1) * limit)
      .limit(limit);

    res.status(200).json({
      success: true,
      data: periods,
      pagination: {
        total,
        page: parseInt(page),
        limit: parseInt(limit),
        totalPages: Math.ceil(total / limit)
      }
    });
  } catch (error) {
    console.error("Get periods error:", error);
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

// Get single period
const getPeriod = async (req, res) => {
  try {
    const period = await Period.findById(req.params.id);
    
    if (!period) {
      return res.status(404).json({
        success: false,
        message: "Period not found"
      });
    }

    res.status(200).json({
      success: true,
      data: period
    });
  } catch (error) {
    console.error("Get period error:", error);
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

// Update period
const updatePeriod = async (req, res) => {
  try {
    const { periodNumber, startTime, endTime } = req.body;

    const updatedPeriod = await Period.findByIdAndUpdate(
      req.params.id,
      { periodNumber, startTime, endTime },
      { new: true, runValidators: true }
    );

    if (!updatedPeriod) {
      return res.status(404).json({
        success: false,
        message: "Period not found"
      });
    }

    res.status(200).json({
      success: true,
      data: updatedPeriod
    });
  } catch (error) {
    console.error("Update period error:", error);
    if (error.code === 11000) {
      return res.status(400).json({
        success: false,
        message: "Period number already exists"
      });
    }
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

// Delete period
const deletePeriod = async (req, res) => {
  try {
    const deletedPeriod = await Period.findByIdAndDelete(req.params.id);

    if (!deletedPeriod) {
      return res.status(404).json({
        success: false,
        message: "Period not found"
      });
    }

    res.status(200).json({
      success: true,
      message: 'Period deleted successfully'
    });
  } catch (error) {
    console.error("Delete period error:", error);
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

module.exports = {
  createPeriod,
  getPeriods,
  getPeriod,
  updatePeriod,
  deletePeriod
}; 