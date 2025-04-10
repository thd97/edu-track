const Joi = require("joi");

const validators = {
  role: Joi.string().valid("admin", "teacher").required().messages({
    "any.only": "Role must be one of: admin or teacher",
    "any.required": "Role is required",
  }),

  username: Joi.string().min(5).max(20).required().messages({
    "string.base": "Username must be a string",
    "string.min": "Username must be at least 5 characters long",
    "string.max": "Username must be at most 20 characters long",
    "any.required": "Username is required",
  }),

  fullName: Joi.string().min(5).required().messages({
    "string.base": "Full name must be a string",
    "string.min": "Full name must be at least 5 characters long",
    "any.required": "Full name is required",
  }),

  gender: Joi.string().valid("male", "female").required().messages({
    "any.only": "Gender must be male or female",
    "any.required": "Gender is required",
  }),

  dateOfBirth: Joi.date().optional().messages({
    "date.base": "Date of birth must be a valid date",
  }),

  email: Joi.string().email().required().messages({
    "string.email": "Email must be a valid email address",
    "any.required": "Email is required",
  }),

  address: Joi.string().min(3).max(255).optional().messages({
    "string.min": "Address must be at least 3 characters long",
    "string.max": "Address must be at most 255 characters long",
  }),

  phoneNumber: Joi.string()
    .pattern(/^[0-9]{9,20}$/)
    .optional()
    .messages({
      "string.pattern.base": "Phone number must contain only digits",
      "string.min": "Phone number must be at least 9 characters long",
      "string.max": "Phone number must be at most 20 characters long",
    }),

  subjectName: Joi.string().min(2).max(100).required().messages({
    "string.base": "Subject name must be a string",
    "string.min": "Subject name must be at least 2 characters long",
    "string.max": "Subject name must be at most 100 characters long",
    "any.required": "Subject name is required",
  }),

  class: Joi.string().required().messages({
    "any.required": "Class ID is required",
    "string.base": "Class ID must be a string",
  }),

  periodNumber: Joi.number().integer().min(1).required().messages({
    "number.base": "Period number must be a number",
    "number.min": "Period number must be 1 or greater",
    "any.required": "Period number is required",
  }),

  startTime: Joi.string()
    .pattern(/^([01]\d|2[0-3]):([0-5]\d)$/)
    .required()
    .messages({
      "string.pattern.base": "Start time must be in HH:mm format (24h)",
      "any.required": "Start time is required",
    }),

  endTime: Joi.string()
    .pattern(/^([01]\d|2[0-3]):([0-5]\d)$/)
    .required()
    .messages({
      "string.pattern.base": "End time must be in HH:mm format (24h)",
      "any.required": "End time is required",
    }),

  examId: Joi.string().required().messages({
    "any.required": "Exam ID is required",
    "string.base": "Exam ID must be a string",
  }),

  score: Joi.number().min(0).max(10).required().messages({
    "number.base": "Score must be a number",
    "number.min": "Score must be at least 0",
    "number.max": "Score must be at most 10",
    "any.required": "Score is required",
  }),

  examName: Joi.string().min(1).required().messages({
    "string.base": "Exam name must be a string",
    "string.empty": "Exam name cannot be empty",
    "any.required": "Exam name is required",
  }),

  examDate: Joi.date().required().messages({
    "date.base": "Exam date must be a valid date",
    "any.required": "Exam date is required",
  }),

  className: Joi.string().min(1).required().messages({
    "string.base": "Class name must be a string",
    "string.empty": "Class name cannot be empty",
    "any.required": "Class name is required",
  }),

  teacher: Joi.string().optional().messages({
    "string.base": "Teacher ID must be a string",
  }),

  student: Joi.string().required().messages({
    "string.base": "Student ID must be a string",
    "any.required": "Student ID is required",
  }),

  date: Joi.date().required().messages({
    "date.base": "Date must be a valid date",
    "any.required": "Date is required",
  }),

  period: Joi.string().required().messages({
    "string.base": "Period ID must be a string",
    "any.required": "Period ID is required",
  }),

  status: Joi.string()
    .valid("present", "absent", "late", "permission")
    .required()
    .messages({
      "any.only": "Status must be one of: present, absent, late, permission",
      "any.required": "Status is required",
    }),

  note: Joi.string().allow("").optional().messages({
    "string.base": "Note must be a string",
  }),

  password: Joi.string().min(4).required().messages({
    "string.base": "Password must be a string",
    "string.min": "Password must be at least 4 characters long",
    "any.required": "Password is required",
  }),
};

const validateFields = (fields) => {
  return (req, res, next) => {
    const selectedSchema = fields.reduce((acc, field) => {
      if (validators[field]) acc[field] = validators[field];
      return acc;
    }, {});

    const schema = Joi.object(selectedSchema);
    const { error } = schema.validate(req.body, { abortEarly: false });

    if (error) {
      const errors = error.details.map((err) => ({
        field: err.context.key,
        message: err.message,
      }));
      return res.status(400).json({ success: false, errors });
    }

    next();
  };
};

module.exports = {
  validators,
  validateFields,
};
