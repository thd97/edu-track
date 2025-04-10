const mongoose = require("mongoose");

const ExamSchema = new mongoose.Schema(
  {
    name: { type: String, required: true, unique: true },
    examDate: { type: Date, required: true },
  },
  { timestamps: true }
);

module.exports = mongoose.model("Exam", ExamSchema);
