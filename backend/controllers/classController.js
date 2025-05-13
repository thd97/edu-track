const Class = require("../models/Class");
const User = require("../models/User");
const Student = require("../models/Student");

// Create new class
const createClass = async (req, res) => {
  try {
    const { name, teacher } = req.body;

    // Check if teacher exists and has teacher role
    const teacherUser = await User.findById(teacher);
    if (!teacherUser || teacherUser.role !== "teacher") {
      return res.status(400).json({
        success: false,
        message: "Teacher must be a user with teacher role"
      });
    }

    const newClass = await Class.create({
      name,
      teacher
    });

    // Populate teacher information
    const populatedClass = await Class.findById(newClass._id)
      .populate("teacher", "username fullName");

    res.status(201).json({
      success: true,
      data: populatedClass
    });
  } catch (error) {
    console.error("Create class error:", error);
    if (error.code === 11000) {
      return res.status(400).json({
        success: false,
        message: "Class name already exists"
      });
    }
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

// Get all classes
const getClasses = async (req, res) => {
  try {
    const { name, teacher, page = 1, limit = 10 } = req.query;
    
    // Build filter query
    const filter = {};
    
    if (name) {
      filter.name = { $regex: name, $options: 'i' };
    }
    
    if (teacher) {
      filter.teacher = teacher;
    }

    const total = await Class.countDocuments(filter);
    const classes = await Class.find(filter)
      .populate("teacher", "username fullName")
      .skip((page - 1) * limit)
      .limit(limit);

    // Lấy danh sách classId
    const classIds = classes.map(cls => cls._id);

    // Đếm số học sinh theo classId
    const studentCounts = await Student.aggregate([
      { $match: { class: { $in: classIds } } },
      { $group: { _id: "$class", total: { $sum: 1 } } }
    ]);

    // Map classId -> totalStudent
    const countMap = {};
    studentCounts.forEach(item => {
      countMap[item._id.toString()] = item.total;
    });

    // Gán totalStudent vào từng class
    const classesWithTotal = classes.map(cls => {
      const obj = cls.toObject();
      obj.totalStudent = countMap[cls._id.toString()] || 0;
      return obj;
    });

    res.status(200).json({
      success: true,
      data: classesWithTotal,
      pagination: {
        total,
        page: parseInt(page),
        limit: parseInt(limit),
        totalPages: Math.ceil(total / limit)
      }
    });
  } catch (error) {
    console.error("Get classes error:", error);
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

// Get single class
const getClass = async (req, res) => {
  try {
    const classData = await Class.findById(req.params.id).populate("teacher", "username fullName");
    
    if (!classData) {
      return res.status(404).json({
        success: false,
        message: "Class not found"
      });
    }

    res.status(200).json({
      success: true,
      data: classData
    });
  } catch (error) {
    console.error("Get class error:", error);
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

// Update class
const updateClass = async (req, res) => {
  try {
    const { name, teacher } = req.body;

    // Check if teacher exists and has teacher role
    if (teacher) {
      const teacherUser = await User.findById(teacher);
      if (!teacherUser || teacherUser.role !== "teacher") {
        return res.status(400).json({
          success: false,
          message: "Teacher must be a user with teacher role"
        });
      }
    }

    const updatedClass = await Class.findByIdAndUpdate(
      req.params.id,
      { name, teacher },
      { new: true, runValidators: true }
    ).populate("teacher", "username fullName");

    if (!updatedClass) {
      return res.status(404).json({
        success: false,
        message: "Class not found"
      });
    }

    res.status(200).json({
      success: true,
      data: updatedClass
    });
  } catch (error) {
    console.error("Update class error:", error);
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

// Delete class
const deleteClass = async (req, res) => {
  try {
    const deletedClass = await Class.findByIdAndDelete(req.params.id);

    if (!deletedClass) {
      return res.status(404).json({
        success: false,
        message: "Class not found"
      });
    }

    res.status(200).json({
      success: true,
      message: 'Class deleted successfully'
    });
  } catch (error) {
    console.error("Delete class error:", error);
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

module.exports = {
  createClass,
  getClasses,
  getClass,
  updateClass,
  deleteClass
}; 