const mongoose = require("mongoose");

const ScheduleSchema = new mongoose.Schema(
  {
    class: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Class",
      required: true,
    },
    subject: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Subject",
      required: true,
    },
    period: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Period",
      required: true,
    },
    dayOfWeek: {
      type: Number,
      required: true,
      min: 1,
      max: 7, // 1: Monday, 2: Tuesday, ..., 7: Sunday
    },
    teacher: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true,
    },
    room: {
      type: String,
      required: true,
    },
    startDate: {
      type: Date,
      required: true,
    },
    endDate: {
      type: Date,
      required: true,
    },
    isActive: {
      type: Boolean,
      default: true,
    },
  },
  { timestamps: true }
);

// Tạo index để đảm bảo không có lịch trùng nhau trong cùng một lớp, tiết học và ngày
ScheduleSchema.index(
  { class: 1, period: 1, dayOfWeek: 1 },
  { unique: true }
);

module.exports = mongoose.model("Schedule", ScheduleSchema); 