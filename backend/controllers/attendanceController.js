const Attendance = require("../models/Attendance");
const Student = require("../models/Student");
const Class = require("../models/Class");
const Period = require("../models/Period");

// Create a new attendance record
exports.createAttendance = async (req, res) => {
  try {
    const { student, class: classId, date, period, status, note } = req.body;

    // Check if student exists
    const studentExists = await Student.findById(student);
    if (!studentExists) {
      return res.status(404).json({ message: "Student not found" });
    }

    // Check if class exists
    const classExists = await Class.findById(classId);
    if (!classExists) {
      return res.status(404).json({ message: "Class not found" });
    }

    // Check if period exists
    const periodExists = await Period.findById(period);
    if (!periodExists) {
      return res.status(404).json({ message: "Period not found" });
    }

    // Validate status
    const validStatuses = ["present", "absent", "late", "permission"];
    if (!validStatuses.includes(status)) {
      return res.status(400).json({ message: "Invalid attendance status" });
    }

    const attendance = new Attendance({
      student,
      class: classId,
      date,
      period,
      status,
      note,
    });

    await attendance.save();
    res.status(201).json(attendance);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Get all attendance records
exports.getAllAttendances = async (req, res) => {
  try {
    const attendances = await Attendance.find()
      .populate("student")
      .populate("class")
      .populate("period");
    res.json(attendances);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Get attendance by ID
exports.getAttendanceById = async (req, res) => {
  try {
    const attendance = await Attendance.findById(req.params.id)
      .populate("student")
      .populate("class")
      .populate("period");
    if (!attendance) {
      return res.status(404).json({ message: "Attendance record not found" });
    }
    res.json(attendance);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Get attendances by class (for teachers)
exports.getAttendancesByClass = async (req, res) => {
  try {
    const { classId } = req.params;
    const teacherId = req.user.id;

    // Kiểm tra xem giáo viên có dạy lớp này không
    const classExists = await Class.findOne({
      _id: classId,
      teacher: teacherId
    });

    if (!classExists) {
      return res.status(403).json({ message: "Bạn không có quyền truy cập lớp học này" });
    }

    // Lấy tất cả học sinh trong lớp
    const students = await Student.find({ class: classId });
    const studentIds = students.map(student => student._id);

    // Lấy điểm danh của tất cả học sinh trong lớp
    const attendances = await Attendance.find({
      student: { $in: studentIds }
    })
      .populate("student")
      .populate("class")
      .populate("period");

    res.json(attendances);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Get attendances by class and date (for teachers)
exports.getAttendancesByClassAndDate = async (req, res) => {
  try {
    const { classId, date } = req.params;
    const teacherId = req.user.id;

    // Kiểm tra xem giáo viên có dạy lớp này không
    const classExists = await Class.findOne({
      _id: classId,
      teacher: teacherId
    });

    if (!classExists) {
      return res.status(403).json({ message: "Bạn không có quyền truy cập lớp học này" });
    }

    // Lấy tất cả học sinh trong lớp
    const students = await Student.find({ class: classId });
    const studentIds = students.map(student => student._id);

    // Lấy điểm danh của tất cả học sinh trong lớp cho ngày cụ thể
    const attendances = await Attendance.find({
      student: { $in: studentIds },
      date: new Date(date)
    })
      .populate("student")
      .populate("class")
      .populate("period");

    res.json(attendances);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Update attendance (for teachers)
exports.updateAttendance = async (req, res) => {
  try {
    const { status } = req.body;
    const teacherId = req.user.id;

    // Lấy thông tin attendance
    const attendance = await Attendance.findById(req.params.id)
      .populate("student");

    if (!attendance) {
      return res.status(404).json({ message: "Không tìm thấy bản ghi điểm danh" });
    }

    // Kiểm tra xem giáo viên có dạy lớp của học sinh này không
    const studentClass = await Class.findOne({
      _id: attendance.student.class,
      teacher: teacherId
    });

    if (!studentClass) {
      return res.status(403).json({ message: "Bạn không có quyền cập nhật bản ghi điểm danh này" });
    }

    // Validate status if provided
    if (status) {
      const validStatuses = ["present", "absent", "late", "permission"];
      if (!validStatuses.includes(status)) {
        return res.status(400).json({ message: "Trạng thái điểm danh không hợp lệ" });
      }
    }

    const updatedAttendance = await Attendance.findByIdAndUpdate(
      req.params.id,
      req.body,
      { new: true }
    )
      .populate("student")
      .populate("class")
      .populate("period");

    res.json(updatedAttendance);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Delete attendance record
exports.deleteAttendance = async (req, res) => {
  try {
    const attendance = await Attendance.findByIdAndDelete(req.params.id);
    if (!attendance) {
      return res.status(404).json({ message: "Attendance record not found" });
    }
    res.json({ message: "Attendance record deleted successfully" });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}; 