const User = require('./models/User');
const bcrypt = require('bcryptjs');

const seedUsers = async () => {
  try {
    // Check if users already exist
    const adminExists = await User.findOne({ username: 'admin' });
    const teacherExists = await User.findOne({ username: 'teacher' });

    if (!adminExists) {
      const admin = new User({
        username: 'admin',
        password: 'admin',
        fullName: 'Admin User',
        role: 'admin',
        email: 'admin@edutrack.com',
        phoneNumber: '0123456789',
        address: 'Admin Address',
        gender: 'male',
        dateOfBirth: new Date('1990-01-01')
      });
      await admin.save();
      console.log('Admin user created');
    }

    if (!teacherExists) {
      const teacher = new User({
        username: 'teacher',
        password: 'teacher',
        fullName: 'Teacher User',
        role: 'teacher',
        email: 'teacher@edutrack.com',
        phoneNumber: '0987654321',
        address: 'Teacher Address',
        gender: 'female',
        dateOfBirth: new Date('1995-01-01')
      });
      await teacher.save();
      console.log('Teacher user created');
    }

    console.log('Seed data completed');
  } catch (error) {
    console.error('Error seeding data:', error);
  }
};

module.exports = seedUsers;
