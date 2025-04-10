const User = require('../models/User');
const jwt = require('jsonwebtoken');
const { validators } = require('../utils/validator');

const createUser = async (req, res) => {
  try {
    const { username, password, fullName, role, email, phoneNumber, address, gender, dateOfBirth } = req.body;

    // Check if username or email already exists
    const existingUser = await User.findOne({
      $or: [{ username }, { email }]
    });

    if (existingUser) {
      return res.status(400).json({
        success: false,
        message: existingUser.username === username ? 'Username already exists' : 'Email already exists'
      });
    }

    const user = new User({
      username,
      password,
      fullName,
      role,
      email,
      phoneNumber,
      address,
      gender,
      dateOfBirth
    });

    await user.save();

    const newUser = await User.findById(user._id).select('-password');

    res.status(201).json({
      success: true,
      data: newUser
    });
  } catch (error) {
    console.error('Create user error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error',
      error: process.env.NODE_ENV === 'development' ? error.message : undefined
    });
  }
};

const login = async (req, res) => {
  try {
    const { username, password } = req.body;

    const user = await User.findOne({ username });
    if (!user) {
      return res.status(401).json({
        success: false,
        message: 'Invalid username or password'
      });
    }

    const isMatch = await user.comparePassword(password);
    if (!isMatch) {
      return res.status(401).json({
        success: false,
        message: 'Invalid username or password'
      });
    }

    const token = jwt.sign(
      { userId: user._id },
      process.env.BE_JWT_SECRET,
      { expiresIn: process.env.BE_JWT_EXPIRES_IN }
    );

    res.status(200).json({
      success: true,
      data: {
        accessToken: token,
        fullName: user.fullName,
        role: user.role
      }
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: 'Server error'
    });
  }
};

const getMe = async (req, res) => {
  try {
    const user = await User.findById(req.user._id).select('-password');
    
    res.status(200).json({
      success: true,
      data: user
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: 'Server error'
    });
  }
};

const updateUser = async (req, res) => {
  try {
    const { fullName, email, phoneNumber, address, gender, dateOfBirth, password } = req.body;

    const user = await User.findById(req.user._id);
    if (!user) {
      return res.status(404).json({
        success: false,
        message: 'User not found'
      });
    }

    // Validate each field if provided
    const validationErrors = [];
    
    if (email !== undefined) {
      const emailError = validators.email.validate(email).error;
      if (emailError) validationErrors.push({ field: 'email', message: emailError.details[0].message });
    }
    
    if (fullName !== undefined) {
      const fullNameError = validators.fullName.validate(fullName).error;
      if (fullNameError) validationErrors.push({ field: 'fullName', message: fullNameError.details[0].message });
    }
    
    if (phoneNumber !== undefined) {
      const phoneError = validators.phoneNumber.validate(phoneNumber).error;
      if (phoneError) validationErrors.push({ field: 'phoneNumber', message: phoneError.details[0].message });
    }
    
    if (address !== undefined) {
      const addressError = validators.address.validate(address).error;
      if (addressError) validationErrors.push({ field: 'address', message: addressError.details[0].message });
    }
    
    if (gender !== undefined) {
      const genderError = validators.gender.validate(gender).error;
      if (genderError) validationErrors.push({ field: 'gender', message: genderError.details[0].message });
    }
    
    if (dateOfBirth !== undefined) {
      const dobError = validators.dateOfBirth.validate(dateOfBirth).error;
      if (dobError) validationErrors.push({ field: 'dateOfBirth', message: dobError.details[0].message });
    }
    
    if (password !== undefined) {
      const passwordError = validators.password.validate(password).error;
      if (passwordError) validationErrors.push({ field: 'password', message: passwordError.details[0].message });
    }

    if (validationErrors.length > 0) {
      return res.status(400).json({
        success: false,
        errors: validationErrors
      });
    }

    // Update allowed fields
    if (email !== undefined) user.email = email;
    if (fullName !== undefined) user.fullName = fullName;
    if (phoneNumber !== undefined) user.phoneNumber = phoneNumber;
    if (address !== undefined) user.address = address;
    if (gender !== undefined) user.gender = gender;
    if (dateOfBirth !== undefined) user.dateOfBirth = dateOfBirth;
    if (password !== undefined) user.password = password;

    await user.save();

    const updatedUser = await User.findById(user._id).select('-password');

    res.status(200).json({
      success: true,
      data: updatedUser
    });
  } catch (error) {
    console.error('Update user error:', error);
    
    // Handle specific errors
    if (error.code === 11000) { // Duplicate key error
      return res.status(400).json({
        success: false,
        message: 'Email already exists'
      });
    }

    res.status(500).json({
      success: false,
      message: 'Server error',
      error: process.env.NODE_ENV === 'development' ? error.message : undefined
    });
  }
};

const updateUserAdmin = async (req, res) => {
  try {
    const { userId, newPassword } = req.body;

    // Check if user exists
    const user = await User.findById(userId);
    if (!user) {
      return res.status(404).json({
        success: false,
        message: 'User not found'
      });
    }

    // Update password
    user.password = newPassword;
    await user.save();

    res.status(200).json({
      success: true,
      message: 'Password updated successfully'
    });
  } catch (error) {
    console.error('Update password error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error',
      error: process.env.NODE_ENV === 'development' ? error.message : undefined
    });
  }
};

const deleteUser = async (req, res) => {
  try {
    const { id } = req.params;

    // Check if user exists
    const userToDelete = await User.findById(id);
    if (!userToDelete) {
      return res.status(404).json({
        success: false,
        message: 'User not found'
      });
    }

    // Check if trying to delete self
    if (userToDelete._id.toString() === req.user._id.toString()) {
      return res.status(400).json({
        success: false,
        message: 'You cannot delete yourself'
      });
    }

    // Check if trying to delete another admin (only super admin can delete other admins)
    if (userToDelete.role === 'admin' && req.user.role !== 'admin') {
      return res.status(403).json({
        success: false,
        message: 'Only admin can delete other admin users'
      });
    }

    await User.findByIdAndDelete(id);

    res.status(200).json({
      success: true,
      message: 'User deleted successfully'
    });
  } catch (error) {
    console.error('Delete user error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error',
      error: process.env.NODE_ENV === 'development' ? error.message : undefined
    });
  }
};

const updateUserByAdmin = async (req, res) => {
  try {
    const { userId } = req.params;
    const { fullName, email, phoneNumber, address, gender, dateOfBirth, password, role } = req.body;

    // Check if user exists
    const user = await User.findById(userId);
    if (!user) {
      return res.status(404).json({
        success: false,
        message: 'User not found'
      });
    }

    // Check if trying to update self's role
    if (user._id.toString() === req.user._id.toString() && role && role !== user.role) {
      return res.status(400).json({
        success: false,
        message: 'You cannot change your own role'
      });
    }

    // Validate each field if provided
    const validationErrors = [];
    
    if (email !== undefined) {
      const emailError = validators.email.validate(email).error;
      if (emailError) validationErrors.push({ field: 'email', message: emailError.details[0].message });
    }
    
    if (fullName !== undefined) {
      const fullNameError = validators.fullName.validate(fullName).error;
      if (fullNameError) validationErrors.push({ field: 'fullName', message: fullNameError.details[0].message });
    }
    
    if (phoneNumber !== undefined) {
      const phoneError = validators.phoneNumber.validate(phoneNumber).error;
      if (phoneError) validationErrors.push({ field: 'phoneNumber', message: phoneError.details[0].message });
    }
    
    if (address !== undefined) {
      const addressError = validators.address.validate(address).error;
      if (addressError) validationErrors.push({ field: 'address', message: addressError.details[0].message });
    }
    
    if (gender !== undefined) {
      const genderError = validators.gender.validate(gender).error;
      if (genderError) validationErrors.push({ field: 'gender', message: genderError.details[0].message });
    }
    
    if (dateOfBirth !== undefined) {
      const dobError = validators.dateOfBirth.validate(dateOfBirth).error;
      if (dobError) validationErrors.push({ field: 'dateOfBirth', message: dobError.details[0].message });
    }
    
    if (password !== undefined) {
      const passwordError = validators.password.validate(password).error;
      if (passwordError) validationErrors.push({ field: 'password', message: passwordError.details[0].message });
    }

    if (role !== undefined) {
      const roleError = validators.role.validate(role).error;
      if (roleError) validationErrors.push({ field: 'role', message: roleError.details[0].message });
    }

    if (validationErrors.length > 0) {
      return res.status(400).json({
        success: false,
        errors: validationErrors
      });
    }

    // Update allowed fields
    if (email !== undefined) user.email = email;
    if (fullName !== undefined) user.fullName = fullName;
    if (phoneNumber !== undefined) user.phoneNumber = phoneNumber;
    if (address !== undefined) user.address = address;
    if (gender !== undefined) user.gender = gender;
    if (dateOfBirth !== undefined) user.dateOfBirth = dateOfBirth;
    if (password !== undefined) user.password = password;
    if (role !== undefined) user.role = role;

    await user.save();

    const updatedUser = await User.findById(user._id).select('-password');

    res.status(200).json({
      success: true,
      data: updatedUser
    });
  } catch (error) {
    console.error('Update user by admin error:', error);
    
    // Handle specific errors
    if (error.code === 11000) { // Duplicate key error
      return res.status(400).json({
        success: false,
        message: 'Email already exists'
      });
    }

    res.status(500).json({
      success: false,
      message: 'Server error',
      error: process.env.NODE_ENV === 'development' ? error.message : undefined
    });
  }
};

// Get all users
const getUsers = async (req, res) => {
  try {
    const { username, fullName, role, email, phoneNumber, address, gender, dateOfBirth, page = 1, limit = 10 } = req.query;
    
    // Build filter query
    const filter = {};
    
    if (username) {
      filter.username = { $regex: username, $options: 'i' };
    }
    
    if (fullName) {
      filter.fullName = { $regex: fullName, $options: 'i' };
    }
    
    if (role) {
      filter.role = role;
    }
    
    if (email) {
      filter.email = { $regex: email, $options: 'i' };
    }
    
    if (phoneNumber) {
      filter.phoneNumber = { $regex: phoneNumber, $options: 'i' };
    }
    
    if (address) {
      filter.address = { $regex: address, $options: 'i' };
    }
    
    if (gender) {
      filter.gender = gender;
    }
    
    if (dateOfBirth) {
      const { from, to } = JSON.parse(dateOfBirth);
      if (from && to) {
        filter.dateOfBirth = { $gte: new Date(from), $lte: new Date(to) };
      } else if (from) {
        filter.dateOfBirth = { $gte: new Date(from) };
      } else if (to) {
        filter.dateOfBirth = { $lte: new Date(to) };
      }
    }

    const total = await User.countDocuments(filter);
    const users = await User.find(filter)
      .select('-password')
      .skip((page - 1) * limit)
      .limit(limit);

    res.status(200).json({
      success: true,
      data: users,
      pagination: {
        total,
        page: parseInt(page),
        limit: parseInt(limit),
        totalPages: Math.ceil(total / limit)
      }
    });
  } catch (error) {
    console.error('Get users error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error',
      error: process.env.NODE_ENV === 'development' ? error.message : undefined
    });
  }
};

// Filter users
const filterUsers = async (req, res) => {
  try {
    const { username, fullName, role, email, phoneNumber, address, gender, dateOfBirth, page = 1, limit = 10 } = req.body;
    
    // Build filter query
    const filter = {};
    
    if (username) {
      filter.username = { $regex: username, $options: 'i' };
    }
    
    if (fullName) {
      filter.fullName = { $regex: fullName, $options: 'i' };
    }
    
    if (role) {
      filter.role = role;
    }
    
    if (email) {
      filter.email = { $regex: email, $options: 'i' };
    }
    
    if (phoneNumber) {
      filter.phoneNumber = { $regex: phoneNumber, $options: 'i' };
    }
    
    if (address) {
      filter.address = { $regex: address, $options: 'i' };
    }
    
    if (gender) {
      filter.gender = gender;
    }
    
    if (dateOfBirth) {
      const { from, to } = dateOfBirth;
      if (from && to) {
        filter.dateOfBirth = { $gte: new Date(from), $lte: new Date(to) };
      } else if (from) {
        filter.dateOfBirth = { $gte: new Date(from) };
      } else if (to) {
        filter.dateOfBirth = { $lte: new Date(to) };
      }
    }

    const total = await User.countDocuments(filter);
    const users = await User.find(filter)
      .select('-password')
      .skip((page - 1) * limit)
      .limit(limit);

    res.status(200).json({
      success: true,
      data: users,
      pagination: {
        total,
        page: parseInt(page),
        limit: parseInt(limit),
        totalPages: Math.ceil(total / limit)
      }
    });
  } catch (error) {
    console.error('Filter users error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error',
      error: process.env.NODE_ENV === 'development' ? error.message : undefined
    });
  }
};

module.exports = {
  login,
  getMe,
  updateUser,
  deleteUser,
  createUser,
  updateUserByAdmin,
  filterUsers,
  getUsers
}; 