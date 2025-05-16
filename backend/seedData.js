const User = require('./models/User');
const Class = require('./models/Class');
const Student = require('./models/Student');
const Subject = require('./models/Subject');
const Exam = require('./models/Exam');
const ExamResult = require('./models/ExamResult');
const Period = require('./models/Period');
const Attendance = require('./models/Attendance');
const Schedule = require('./models/Schedule');
const bcrypt = require('bcryptjs');

const seedData = async () => {
  try {
    // Clear all collections
    console.log('Clearing all collections...');
    await Promise.all([
      User.deleteMany({}),
      Class.deleteMany({}),
      Student.deleteMany({}),
      Subject.deleteMany({}),
      Exam.deleteMany({}),
      ExamResult.deleteMany({}),
      Period.deleteMany({}),
      Attendance.deleteMany({}),
      Schedule.deleteMany({})
    ]);
    console.log('All collections cleared successfully');

    const adminPassword = await bcrypt.hash('admin', 10);
    const teacherPassword = await bcrypt.hash('teacher', 10);

    const admin = await User.findOneAndUpdate(
      { username: 'admin' },
      {
        username: 'admin',
        password: adminPassword,
        fullName: 'Admin User',
        role: 'admin',
        email: 'admin@edutrack.com',
        phoneNumber: '0123456789',
        address: 'Admin Address',
        gender: 'male',
        dateOfBirth: new Date('1990-01-01')
      },
      { upsert: true, new: true }
    );

    const teacher = await User.findOneAndUpdate(
      { username: 'teacher' },
      {
        username: 'teacher',
        password: teacherPassword,
        fullName: 'Teacher User',
        role: 'teacher',
        email: 'teacher@edutrack.com',
        phoneNumber: '0987654321',
        address: 'Teacher Address',
        gender: 'female',
        dateOfBirth: new Date('1995-01-01')
      },
      { upsert: true, new: true }
    );

    // Seed Classes
    const class10A = await Class.findOneAndUpdate(
      { name: '10A' },
      { name: '10A', teacher: teacher._id },
      { upsert: true, new: true }
    );

    const class10B = await Class.findOneAndUpdate(
      { name: '10B' },
      { name: '10B', teacher: teacher._id },
      { upsert: true, new: true }
    );

    // Seed Subjects
    const subjects = [
      { name: 'Mathematics' },
      { name: 'Physics' },
      { name: 'Chemistry' },
      { name: 'Biology' },
      { name: 'Literature' },
      { name: 'English' },
      { name: 'History' },
      { name: 'Geography' }
    ];

    const createdSubjects = await Promise.all(
      subjects.map(subject =>
        Subject.findOneAndUpdate(
          { name: subject.name },
          subject,
          { upsert: true, new: true }
        )
      )
    );

    // Seed Students
    const students = [
      {
        fullName: 'Nguyen Van A',
        gender: 'male',
        dateOfBirth: new Date('2005-01-15'),
        address: '123 Street A, City',
        phoneNumber: '0123456781',
        class: class10A._id
      },
      {
        fullName: 'Tran Thi B',
        gender: 'female',
        dateOfBirth: new Date('2005-03-20'),
        address: '456 Street B, City',
        phoneNumber: '0123456782',
        class: class10A._id
      },
      {
        fullName: 'Le Van C',
        gender: 'male',
        dateOfBirth: new Date('2005-06-10'),
        address: '789 Street C, City',
        phoneNumber: '0123456783',
        class: class10B._id
      }
    ];

    const createdStudents = await Promise.all(
      students.map(student =>
        Student.findOneAndUpdate(
          { fullName: student.fullName },
          student,
          { upsert: true, new: true }
        )
      )
    );

    // Seed Periods
    const periods = [
      { periodNumber: 1, startTime: '07:00', endTime: '07:45' },
      { periodNumber: 2, startTime: '07:50', endTime: '08:35' },
      { periodNumber: 3, startTime: '08:40', endTime: '09:25' },
      { periodNumber: 4, startTime: '09:30', endTime: '10:15' },
      { periodNumber: 5, startTime: '10:20', endTime: '11:05' },
      { periodNumber: 6, startTime: '11:10', endTime: '11:55' }
    ];

    const createdPeriods = await Promise.all(
      periods.map(period =>
        Period.findOneAndUpdate(
          { periodNumber: period.periodNumber },
          period,
          { upsert: true, new: true }
        )
      )
    );

    // Seed Exams
    const exams = [
      {
        name: 'Midterm Exam',
        examDate: new Date('2024-03-15')
      },
      {
        name: 'Final Exam',
        examDate: new Date('2024-06-15')
      }
    ];

    const createdExams = await Promise.all(
      exams.map(exam =>
        Exam.findOneAndUpdate(
          { name: exam.name },
          exam,
          { upsert: true, new: true }
        )
      )
    );

    // Seed Exam Results
    const examResults = [];
    for (const student of createdStudents) {
      for (const exam of createdExams) {
        examResults.push({
          exam: exam._id,
          student: student._id,
          score: Math.floor(Math.random() * 11)
        });
      }
    }

    await Promise.all(
      examResults.map(result =>
        ExamResult.findOneAndUpdate(
          { exam: result.exam, student: result.student },
          result,
          { upsert: true }
        )
      )
    );

    // Seed Schedules
    const schedules = [];
    const startDate = new Date('2024-02-01');
    const endDate = new Date('2024-06-30');

    // Create schedules for each class
    for (const classItem of [class10A, class10B]) {
      for (let dayOfWeek = 1; dayOfWeek <= 5; dayOfWeek++) {
        for (const period of createdPeriods) {
          const subject = createdSubjects[Math.floor(Math.random() * createdSubjects.length)];
          schedules.push({
            class: classItem._id,
            subject: subject._id,
            period: period._id,
            dayOfWeek,
            teacher: teacher._id,
            room: `Room ${Math.floor(Math.random() * 10) + 1}`,
            startDate,
            endDate,
            isActive: true
          });
        }
      }
    }

    await Promise.all(
      schedules.map(schedule =>
        Schedule.findOneAndUpdate(
          {
            class: schedule.class,
            period: schedule.period,
            dayOfWeek: schedule.dayOfWeek
          },
          schedule,
          { upsert: true }
        )
      )
    );

    // Seed Attendance
    const attendanceRecords = [];
    const today = new Date();
    for (const student of createdStudents) {
      for (const period of createdPeriods) {
        attendanceRecords.push({
          student: student._id,
          class: student.class,
          date: today,
          period: period._id,
          status: ['present', 'absent', 'late', 'permission'][Math.floor(Math.random() * 4)],
          note: 'Sample attendance record'
        });
      }
    }

    await Promise.all(
      attendanceRecords.map(record =>
        Attendance.findOneAndUpdate(
          {
            student: record.student,
            class: record.class,
            date: record.date,
            period: record.period
          },
          record,
          { upsert: true }
        )
      )
    );

    console.log('Seed data completed successfully');
  } catch (error) {
    console.error('Error seeding data:', error);
  }
};

module.exports = seedData;
