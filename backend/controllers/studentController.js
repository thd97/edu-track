const Student = require("../models/Student");
const Class = require("../models/Class");

// Create new student
const createStudent = async (req, res) => {
  try {
    const { fullName, gender, dateOfBirth, address, phoneNumber, class: classId } = req.body;

    // Check if class exists
    const classExists = await Class.findById(classId);
    if (!classExists) {
      return res.status(400).json({
        success: false,
        message: "Class not found"
      });
    }

    const newStudent = await Student.create({
      fullName,
      gender,
      dateOfBirth,
      address,
      phoneNumber,
      class: classId
    });

    // Populate class information
    const populatedStudent = await Student.findById(newStudent._id)
      .populate("class", "name teacher");

    res.status(201).json({
      success: true,
      data: populatedStudent
    });
  } catch (error) {
    console.error("Create student error:", error);
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

// Get all students
const getStudents = async (req, res) => {
  try {
    const { fullName, gender, dateOfBirth, address, phoneNumber, class: classId, page = 1, limit = 10 } = req.query;
    
    // Build filter query
    const filter = {};
    
    if (fullName) {
      filter.fullName = { $regex: fullName, $options: 'i' };
    }
    
    if (gender) {
      filter.gender = gender;
    }
    
    if (dateOfBirth) {
      const { from, to } = JSON.parse(dateOfBirth);
      if (from && to) {
        filter.dateOfBirth = { $gte: new Date(from), $lte: new Date(to) };
      } else if (from) {
        filter.dateOfBirth = { $gte: new Date(from) };
      } else if (to) {
        filter.dateOfBirth = { $lte: new Date(to) };
      }
    }
    
    if (address) {
      filter.address = { $regex: address, $options: 'i' };
    }
    
    if (phoneNumber) {
      filter.phoneNumber = { $regex: phoneNumber, $options: 'i' };
    }
    
    if (classId) {
      filter.class = classId;
    }

    const total = await Student.countDocuments(filter);
    const students = await Student.find(filter)
      .populate("class", "name teacher")
      .skip((page - 1) * limit)
      .limit(limit);

    res.status(200).json({
      success: true,
      data: students,
      pagination: {
        total,
        page: parseInt(page),
        limit: parseInt(limit),
        totalPages: Math.ceil(total / limit)
      }
    });
  } catch (error) {
    console.error("Get students error:", error);
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

// Get single student
const getStudent = async (req, res) => {
  try {
    const student = await Student.findById(req.params.id)
      .populate("class", "name teacher");
    
    if (!student) {
      return res.status(404).json({
        success: false,
        message: "Student not found"
      });
    }

    res.status(200).json({
      success: true,
      data: student
    });
  } catch (error) {
    console.error("Get student error:", error);
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

// Update student
const updateStudent = async (req, res) => {
  try {
    const { fullName, gender, dateOfBirth, address, phoneNumber, class: classId } = req.body;

    // Check if class exists if class is being updated
    if (classId) {
      const classExists = await Class.findById(classId);
      if (!classExists) {
        return res.status(400).json({
          success: false,
          message: "Class not found"
        });
      }
    }

    const updatedStudent = await Student.findByIdAndUpdate(
      req.params.id,
      { fullName, gender, dateOfBirth, address, phoneNumber, class: classId },
      { new: true, runValidators: true }
    ).populate("class", "name teacher");

    if (!updatedStudent) {
      return res.status(404).json({
        success: false,
        message: "Student not found"
      });
    }

    res.status(200).json({
      success: true,
      data: updatedStudent
    });
  } catch (error) {
    console.error("Update student error:", error);
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

// Delete student
const deleteStudent = async (req, res) => {
  try {
    const deletedStudent = await Student.findByIdAndDelete(req.params.id);

    if (!deletedStudent) {
      return res.status(404).json({
        success: false,
        message: "Student not found"
      });
    }

    res.status(200).json({
      success: true,
      data: {}
    });
  } catch (error) {
    console.error("Delete student error:", error);
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

// Get students by class ID
const getStudentsByClass = async (req, res) => {
  try {
    const { classId } = req.params;
    const { fullName, gender, dateOfBirth, address, phoneNumber, page = 1, limit = 10 } = req.query;
    const userId = req.user.id;
    const userRole = req.user.role;

    // Check if class exists
    const classExists = await Class.findById(classId);
    if (!classExists) {
      return res.status(404).json({
        success: false,
        message: "Class not found"
      });
    }

    // If user is teacher, check if they teach this class
    if (userRole === "teacher" && classExists.teacher.toString() !== userId) {
      return res.status(403).json({
        success: false,
        message: "You don't have permission to view students in this class"
      });
    }

    // Build filter query
    const filter = { class: classId };
    
    if (fullName) {
      filter.fullName = { $regex: fullName, $options: 'i' };
    }
    
    if (gender) {
      filter.gender = gender;
    }
    
    if (dateOfBirth) {
      const { from, to } = JSON.parse(dateOfBirth);
      if (from && to) {
        filter.dateOfBirth = { $gte: new Date(from), $lte: new Date(to) };
      } else if (from) {
        filter.dateOfBirth = { $gte: new Date(from) };
      } else if (to) {
        filter.dateOfBirth = { $lte: new Date(to) };
      }
    }
    
    if (address) {
      filter.address = { $regex: address, $options: 'i' };
    }
    
    if (phoneNumber) {
      filter.phoneNumber = { $regex: phoneNumber, $options: 'i' };
    }

    const total = await Student.countDocuments(filter);
    const students = await Student.find(filter)
      .populate("class", "name teacher")
      .skip((page - 1) * limit)
      .limit(limit);

    res.status(200).json({
      success: true,
      data: students,
      pagination: {
        total,
        page: parseInt(page),
        limit: parseInt(limit),
        totalPages: Math.ceil(total / limit)
      }
    });
  } catch (error) {
    console.error("Get students by class error:", error);
    res.status(500).json({
      success: false,
      message: "Server error",
      error: process.env.NODE_ENV === "development" ? error.message : undefined
    });
  }
};

// Filter students
const filterStudents = async (req, res) => {
  try {
    const { fullName, gender, dateOfBirth, address, phoneNumber, class: classId, page = 1, limit = 10 } = req.body;
    
    // Build filter query
    const filter = {};
    
    if (fullName) {
      filter.fullName = { $regex: fullName, $options: 'i' };
    }
    
    if (gender) {
      filter.gender = gender;
    }
    
    if (dateOfBirth) {
      const { from, to } = dateOfBirth;
      if (from && to) {
        filter.dateOfBirth = { $gte: new Date(from), $lte: new Date(to) };
      } else if (from) {
        filter.dateOfBirth = { $gte: new Date(from) };
      } else if (to) {
        filter.dateOfBirth = { $lte: new Date(to) };
      }
    }
    
    if (address) {
      filter.address = { $regex: address, $options: 'i' };
    }
    
    if (phoneNumber) {
      filter.phoneNumber = { $regex: phoneNumber, $options: 'i' };
    }
    
    if (classId) {
      filter.class = classId;
    }

    const total = await Student.countDocuments(filter);
    const students = await Student.find(filter)
      .populate("class", "name teacher")
      .skip((page - 1) * limit)
      .limit(limit);

    res.status(200).json({
      success: true,
      data: students,
      pagination: {
        total,
        page: parseInt(page),
        limit: parseInt(limit),
        totalPages: Math.ceil(total / limit)
      }
    });
  } catch (error) {
    console.error('Filter students error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error',
      error: process.env.NODE_ENV === 'development' ? error.message : undefined
    });
  }
};

// Filter students for teacher
const filterStudentsForTeacher = async (req, res) => {
  try {
    const { fullName, gender, dateOfBirth, address, phoneNumber, class: classId } = req.body;
    const teacherId = req.user.id;
    
    // Build filter query
    const filter = {};
    
    if (fullName) {
      filter.fullName = { $regex: fullName, $options: 'i' };
    }
    
    if (gender) {
      filter.gender = gender;
    }
    
    if (dateOfBirth) {
      const { from, to } = dateOfBirth;
      if (from && to) {
        filter.dateOfBirth = { $gte: new Date(from), $lte: new Date(to) };
      } else if (from) {
        filter.dateOfBirth = { $gte: new Date(from) };
      } else if (to) {
        filter.dateOfBirth = { $lte: new Date(to) };
      }
    }
    
    if (address) {
      filter.address = { $regex: address, $options: 'i' };
    }
    
    if (phoneNumber) {
      filter.phoneNumber = { $regex: phoneNumber, $options: 'i' };
    }

    // Get all classes taught by this teacher
    const teacherClasses = await Class.find({ teacher: teacherId });
    const classIds = teacherClasses.map(c => c._id);

    // If specific class is provided, check if teacher teaches that class
    if (classId) {
      if (!classIds.includes(classId)) {
        return res.status(403).json({
          success: false,
          message: "You don't have permission to view students in this class"
        });
      }
      filter.class = classId;
    } else {
      // If no specific class, filter by all classes taught by teacher
      filter.class = { $in: classIds };
    }

    const students = await Student.find(filter)
      .populate("class", "name teacher");

    res.status(200).json({
      success: true,
      data: students
    });
  } catch (error) {
    console.error('Filter students for teacher error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error',
      error: process.env.NODE_ENV === 'development' ? error.message : undefined
    });
  }
};

module.exports = {
  createStudent,
  getStudents,
  getStudent,
  updateStudent,
  deleteStudent,
  getStudentsByClass,
  filterStudents,
  filterStudentsForTeacher
}; 