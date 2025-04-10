const Subject = require('../models/Subject');

// Create new subject
const createSubject = async (req, res) => {
  try {
    const { name } = req.body;

    const newSubject = await Subject.create({
      name
    });

    res.status(201).json({
      success: true,
      data: newSubject
    });
  } catch (error) {
    console.error("Create subject error:", error);
    if (error.code === 11000) {
      return res.status(400).json({
        success: false,
        message: "Subject name already exists"
      });
    }
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

// Get all subjects
const getSubjects = async (req, res) => {
  try {
    const { name, page = 1, limit = 10 } = req.query;
    
    // Build filter query
    const filter = {};
    
    if (name) {
      filter.name = { $regex: name, $options: 'i' };
    }

    const total = await Subject.countDocuments(filter);
    const subjects = await Subject.find(filter)
      .skip((page - 1) * limit)
      .limit(limit);

    res.status(200).json({
      success: true,
      data: subjects,
      pagination: {
        total,
        page: parseInt(page),
        limit: parseInt(limit),
        totalPages: Math.ceil(total / limit)
      }
    });
  } catch (error) {
    console.error("Get subjects error:", error);
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

// Get single subject
const getSubject = async (req, res) => {
  try {
    const subject = await Subject.findById(req.params.id);
    
    if (!subject) {
      return res.status(404).json({
        success: false,
        message: "Subject not found"
      });
    }

    res.status(200).json({
      success: true,
      data: subject
    });
  } catch (error) {
    console.error("Get subject error:", error);
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

// Update subject
const updateSubject = async (req, res) => {
  try {
    const { name } = req.body;

    const updatedSubject = await Subject.findByIdAndUpdate(
      req.params.id,
      { name },
      { new: true, runValidators: true }
    );

    if (!updatedSubject) {
      return res.status(404).json({
        success: false,
        message: "Subject not found"
      });
    }

    res.status(200).json({
      success: true,
      data: updatedSubject
    });
  } catch (error) {
    console.error("Update subject error:", error);
    if (error.code === 11000) {
      return res.status(400).json({
        success: false,
        message: "Subject name already exists"
      });
    }
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

// Delete subject
const deleteSubject = async (req, res) => {
  try {
    const deletedSubject = await Subject.findByIdAndDelete(req.params.id);

    if (!deletedSubject) {
      return res.status(404).json({
        success: false,
        message: "Subject not found"
      });
    }

    res.status(200).json({
      success: true,
      message: 'Subject deleted successfully'
    });
  } catch (error) {
    console.error("Delete subject error:", error);
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

module.exports = {
  createSubject,
  getSubjects,
  getSubject,
  updateSubject,
  deleteSubject
}; 