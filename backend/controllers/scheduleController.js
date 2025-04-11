const Schedule = require("../models/Schedule");
const Class = require("../models/Class");
const Subject = require("../models/Subject");
const Period = require("../models/Period");
const User = require("../models/User");

// Tạo thời khóa biểu mới
exports.createSchedule = async (req, res) => {
  try {
    const {
      class: classId,
      subject,
      period,
      dayOfWeek,
      teacher,
      room,
      startDate,
      endDate,
    } = req.body;

    // Kiểm tra lớp học tồn tại
    const classExists = await Class.findById(classId);
    if (!classExists) {
      return res.status(404).json({ message: "Không tìm thấy lớp học" });
    }

    // Kiểm tra môn học tồn tại
    const subjectExists = await Subject.findById(subject);
    if (!subjectExists) {
      return res.status(404).json({ message: "Không tìm thấy môn học" });
    }

    // Kiểm tra tiết học tồn tại
    const periodExists = await Period.findById(period);
    if (!periodExists) {
      return res.status(404).json({ message: "Không tìm thấy tiết học" });
    }

    // Kiểm tra giáo viên tồn tại và có vai trò là giáo viên
    const teacherExists = await User.findOne({
      _id: teacher,
      role: "teacher",
    });
    if (!teacherExists) {
      return res.status(404).json({ message: "Không tìm thấy giáo viên" });
    }

    // Kiểm tra xem đã có lịch trùng chưa
    const existingSchedule = await Schedule.findOne({
      class: classId,
      period,
      dayOfWeek,
    });

    if (existingSchedule) {
      return res.status(400).json({
        message: "Đã có lịch học cho lớp này vào tiết học này trong ngày",
      });
    }

    const schedule = new Schedule({
      class: classId,
      subject,
      period,
      dayOfWeek,
      teacher,
      room,
      startDate,
      endDate,
    });

    await schedule.save();
    res.status(201).json(schedule);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Lấy tất cả thời khóa biểu
exports.getAllSchedules = async (req, res) => {
  try {
    const schedules = await Schedule.find()
      .populate("class")
      .populate("subject")
      .populate("period")
      .populate("teacher");
    res.json(schedules);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Lấy thời khóa biểu theo ID
exports.getScheduleById = async (req, res) => {
  try {
    const schedule = await Schedule.findById(req.params.id)
      .populate("class")
      .populate("subject")
      .populate("period")
      .populate("teacher");
    if (!schedule) {
      return res.status(404).json({ message: "Không tìm thấy thời khóa biểu" });
    }
    res.json(schedule);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Lấy thời khóa biểu theo lớp học
exports.getSchedulesByClass = async (req, res) => {
  try {
    const { classId } = req.params;
    const schedules = await Schedule.find({ class: classId })
      .populate("subject")
      .populate("period")
      .populate("teacher");
    res.json(schedules);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Lấy thời khóa biểu theo giáo viên
exports.getSchedulesByTeacher = async (req, res) => {
  try {
    const { teacherId } = req.params;
    const schedules = await Schedule.find({ teacher: teacherId })
      .populate("class")
      .populate("subject")
      .populate("period");
    res.json(schedules);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Cập nhật thời khóa biểu
exports.updateSchedule = async (req, res) => {
  try {
    const {
      subject,
      period,
      dayOfWeek,
      teacher,
      room,
      startDate,
      endDate,
      isActive,
    } = req.body;

    // Kiểm tra các tham số nếu được cung cấp
    if (subject) {
      const subjectExists = await Subject.findById(subject);
      if (!subjectExists) {
        return res.status(404).json({ message: "Không tìm thấy môn học" });
      }
    }

    if (period) {
      const periodExists = await Period.findById(period);
      if (!periodExists) {
        return res.status(404).json({ message: "Không tìm thấy tiết học" });
      }
    }

    if (teacher) {
      const teacherExists = await User.findOne({
        _id: teacher,
        role: "teacher",
      });
      if (!teacherExists) {
        return res.status(404).json({ message: "Không tìm thấy giáo viên" });
      }
    }

    const schedule = await Schedule.findByIdAndUpdate(
      req.params.id,
      req.body,
      { new: true }
    )
      .populate("class")
      .populate("subject")
      .populate("period")
      .populate("teacher");

    if (!schedule) {
      return res.status(404).json({ message: "Không tìm thấy thời khóa biểu" });
    }
    res.json(schedule);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Xóa thời khóa biểu
exports.deleteSchedule = async (req, res) => {
  try {
    const schedule = await Schedule.findByIdAndDelete(req.params.id);
    if (!schedule) {
      return res.status(404).json({ message: "Không tìm thấy thời khóa biểu" });
    }
    res.json({ message: "Đã xóa thời khóa biểu thành công" });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}; 