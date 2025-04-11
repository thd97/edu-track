const mongoose = require("mongoose");

const ExamResultSchema = new mongoose.Schema(
  {
    exam: { type: mongoose.Schema.Types.ObjectId, ref: "Exam", required: true },
    student: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Student",
      required: true,
    },
    score: {
      type: Number,
      required: true,
      min: 0,
      max: 10,
    },
  },
  { timestamps: true }
);

module.exports = mongoose.model("ExamResult", ExamResultSchema);
