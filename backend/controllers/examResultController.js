const ExamResult = require("../models/ExamResult");
const Exam = require("../models/Exam");
const Student = require("../models/Student");
const Class = require("../models/Class");

// Create a new exam result
exports.createExamResult = async (req, res) => {
  try {
    const { exam, student, score } = req.body;

    // Check if exam exists
    const examExists = await Exam.findById(exam);
    if (!examExists) {
      return res.status(404).json({ message: "Exam not found" });
    }

    // Check if student exists
    const studentExists = await Student.findById(student);
    if (!studentExists) {
      return res.status(404).json({ message: "Student not found" });
    }

    // Check if score is valid
    if (score < 0 || score > 10) {
      return res.status(400).json({ message: "Score must be between 0 and 10" });
    }

    const examResult = new ExamResult({
      exam,
      student,
      score,
    });

    await examResult.save();
    res.status(201).json(examResult);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Get all exam results
exports.getAllExamResults = async (req, res) => {
  try {
    const examResults = await ExamResult.find()
      .populate("exam")
      .populate("student");
    res.json(examResults);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Get exam result by ID
exports.getExamResultById = async (req, res) => {
  try {
    const examResult = await ExamResult.findById(req.params.id)
      .populate("exam")
      .populate("student");
    if (!examResult) {
      return res.status(404).json({ message: "Exam result not found" });
    }
    res.json(examResult);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Get exam results by class (for teachers)
exports.getExamResultsByClass = async (req, res) => {
  try {
    const { classId } = req.params;
    const teacherId = req.user.id; // Lấy ID của giáo viên từ token

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

    // Lấy kết quả thi của tất cả học sinh trong lớp
    const examResults = await ExamResult.find({
      student: { $in: studentIds }
    })
      .populate("exam")
      .populate("student");

    res.json(examResults);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Get exam results by class and exam (for teachers)
exports.getExamResultsByClassAndExam = async (req, res) => {
  try {
    const { classId, examId } = req.params;
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

    // Lấy kết quả thi của tất cả học sinh trong lớp cho bài thi cụ thể
    const examResults = await ExamResult.find({
      student: { $in: studentIds },
      exam: examId
    })
      .populate("exam")
      .populate("student");

    res.json(examResults);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Update exam result (for teachers)
exports.updateExamResult = async (req, res) => {
  try {
    const { score } = req.body;
    const teacherId = req.user.id;

    // Lấy thông tin exam result
    const examResult = await ExamResult.findById(req.params.id)
      .populate("student");

    if (!examResult) {
      return res.status(404).json({ message: "Không tìm thấy kết quả thi" });
    }

    // Kiểm tra xem giáo viên có dạy lớp của học sinh này không
    const studentClass = await Class.findOne({
      _id: examResult.student.class,
      teacher: teacherId
    });

    if (!studentClass) {
      return res.status(403).json({ message: "Bạn không có quyền cập nhật kết quả thi này" });
    }

    // Validate score
    if (score && (score < 0 || score > 10)) {
      return res.status(400).json({ message: "Điểm phải từ 0 đến 10" });
    }

    const updatedExamResult = await ExamResult.findByIdAndUpdate(
      req.params.id,
      req.body,
      { new: true }
    )
      .populate("exam")
      .populate("student");

    res.json(updatedExamResult);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Delete exam result
exports.deleteExamResult = async (req, res) => {
  try {
    const examResult = await ExamResult.findByIdAndDelete(req.params.id);
    if (!examResult) {
      return res.status(404).json({ message: "Exam result not found" });
    }
    res.json({ message: "Exam result deleted successfully" });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}; 