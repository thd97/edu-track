const Exam = require('../models/Exam');

// Create new exam
const createExam = async (req, res) => {
  try {
    const { name, examDate } = req.body;

    // Validate examDate
    if (!examDate || isNaN(new Date(examDate).getTime())) {
      return res.status(400).json({
        success: false,
        message: "Invalid exam date format. Please use ISO date string (e.g., '2024-04-10T00:00:00.000Z')"
      });
    }

    const newExam = await Exam.create({
      name,
      examDate: new Date(examDate)
    });

    res.status(201).json({
      success: true,
      data: newExam
    });
  } catch (error) {
    console.error("Create exam error:", error);
    if (error.code === 11000) {
      return res.status(400).json({
        success: false,
        message: "Exam name already exists"
      });
    }
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

// Get all exams
const getExams = async (req, res) => {
  try {
    const { name, examDate, page = 1, limit = 10 } = req.query;
    
    // Build filter query
    const filter = {};
    
    if (name) {
      filter.name = { $regex: name, $options: 'i' };
    }
    
    if (examDate) {
      try {
        const { from, to } = JSON.parse(examDate);
        if (from && to) {
          filter.examDate = { 
            $gte: new Date(from), 
            $lte: new Date(to) 
          };
        } else if (from) {
          filter.examDate = { $gte: new Date(from) };
        } else if (to) {
          filter.examDate = { $lte: new Date(to) };
        }
      } catch (error) {
        return res.status(400).json({
          success: false,
          message: "Invalid date range format. Please use JSON string with 'from' and 'to' dates in ISO format"
        });
      }
    }

    const total = await Exam.countDocuments(filter);
    const exams = await Exam.find(filter)
      .skip((page - 1) * limit)
      .limit(limit);

    res.status(200).json({
      success: true,
      data: exams,
      pagination: {
        total,
        page: parseInt(page),
        limit: parseInt(limit),
        totalPages: Math.ceil(total / limit)
      }
    });
  } catch (error) {
    console.error("Get exams error:", error);
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

// Get single exam
const getExam = async (req, res) => {
  try {
    const exam = await Exam.findById(req.params.id);
    
    if (!exam) {
      return res.status(404).json({
        success: false,
        message: "Exam not found"
      });
    }

    res.status(200).json({
      success: true,
      data: exam
    });
  } catch (error) {
    console.error("Get exam error:", error);
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

// Update exam
const updateExam = async (req, res) => {
  try {
    const { name, examDate } = req.body;

    // Validate examDate if provided
    if (examDate && isNaN(new Date(examDate).getTime())) {
      return res.status(400).json({
        success: false,
        message: "Invalid exam date format. Please use ISO date string (e.g., '2024-04-10T00:00:00.000Z')"
      });
    }

    const updateData = { name };
    if (examDate) {
      updateData.examDate = new Date(examDate);
    }

    const updatedExam = await Exam.findByIdAndUpdate(
      req.params.id,
      updateData,
      { new: true, runValidators: true }
    );

    if (!updatedExam) {
      return res.status(404).json({
        success: false,
        message: "Exam not found"
      });
    }

    res.status(200).json({
      success: true,
      data: updatedExam
    });
  } catch (error) {
    console.error("Update exam error:", error);
    if (error.code === 11000) {
      return res.status(400).json({
        success: false,
        message: "Exam name already exists"
      });
    }
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

// Delete exam
const deleteExam = async (req, res) => {
  try {
    const deletedExam = await Exam.findByIdAndDelete(req.params.id);

    if (!deletedExam) {
      return res.status(404).json({
        success: false,
        message: "Exam not found"
      });
    }

    res.status(200).json({
      success: true,
      message: 'Exam deleted successfully'
    });
  } catch (error) {
    console.error("Delete exam error:", error);
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

module.exports = {
  createExam,
  getExams,
  getExam,
  updateExam,
  deleteExam
}; 