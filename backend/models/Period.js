const mongoose = require("mongoose");

const PeriodSchema = new mongoose.Schema(
  {
    periodNumber: { type: Number, required: true, unique: true },
    startTime: { type: String, required: true },
    endTime: { type: String, required: true },
  },
  { timestamps: true }
);

module.exports = mongoose.model("Period", PeriodSchema);
