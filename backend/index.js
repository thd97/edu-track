require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');

// Import routes and seed data
const userRoutes = require('./routes/userRoutes');
const classRoutes = require("./routes/classRoutes");
const studentRoutes = require("./routes/studentRoutes");
const subjectRoutes = require("./routes/subjectRoutes");
const periodRoutes = require("./routes/periodRoutes");
const examRoutes = require("./routes/examRoutes");
const examResultRoutes = require('./routes/examResultRoutes');
const attendanceRoutes = require('./routes/attendanceRoutes');
const scheduleRoutes = require('./routes/scheduleRoutes');


const seedUsers = require('./seedData');

const app = express();

// Middleware
app.use(cors());
app.use(express.json());


// Routes
app.use('/api/users', userRoutes);
app.use("/api/classes", classRoutes);
app.use("/api/students", studentRoutes);
app.use("/api/subjects", subjectRoutes);
app.use("/api/periods", periodRoutes);
app.use("/api/exams", examRoutes);
app.use('/api/exam-results', examResultRoutes);
app.use('/api/attendances', attendanceRoutes);
app.use('/api/schedules', scheduleRoutes);


// MongoDB connection
mongoose.connect(process.env.BE_MONGODB_URI)
  .then(async () => {
    console.log('Connected to MongoDB');
    
    // Run seed data if enabled
    if (process.env.BE_RUN_SEED === 'true') {
      console.log('Running seed data...');
      await seedUsers();
    }
    
    // Start server
    const PORT = process.env.BE_PORT || 5000;
    app.listen(PORT, () => {
      console.log(`Server is running on port ${PORT}`);
    });
  })
  .catch((error) => {
    console.error('MongoDB connection error:', error);
    process.exit(1);
  });

// Error handling middleware
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({
    success: false,
    message: 'Something went wrong!'
  });
});
