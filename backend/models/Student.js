const mongoose = require("mongoose");

const StudentSchema = new mongoose.Schema(
  {
    fullName: { type: String, required: true },
    gender: { type: String, enum: ["male", "female"], required: true },
    dateOfBirth: { type: Date },
    address: { type: String },
    phoneNumber: { type: String },
    class: { type: mongoose.Schema.Types.ObjectId, ref: "Class", require: true },
  },
  { timestamps: true }
);

module.exports = mongoose.model("Student", StudentSchema);
