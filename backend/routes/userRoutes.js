const express = require("express");
const router = express.Router();
const {
  login,
  getMe,
  updateUser,
  deleteUser,
  createUser,
  updateUserByAdmin,
  filterUsers,
  getUsers
} = require("../controllers/userController");
const { auth, isAdmin, isTeacher } = require("../middleware/auth");
const { validateFields } = require("../utils/validator");

/**
 * @swagger
 * /api/users:
 *   post:
 *     summary: Create a new user (admin only)
 *     tags: [Users]
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - username
 *               - password
 *               - fullName
 *               - role
 *               - email
 *             properties:
 *               username:
 *                 type: string
 *               password:
 *                 type: string
 *               fullName:
 *                 type: string
 *               role:
 *                 type: string
 *                 enum: [admin, teacher]
 *               email:
 *                 type: string
 *               phoneNumber:
 *                 type: string
 *               address:
 *                 type: string
 *               gender:
 *                 type: string
 *                 enum: [male, female]
 *               dateOfBirth:
 *                 type: string
 *                 format: date
 *     responses:
 *       201:
 *         description: User created successfully
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 success:
 *                   type: boolean
 *                 data:
 *                   $ref: '#/components/schemas/User'
 *       400:
 *         description: Bad request (username/email already exists)
 *       401:
 *         description: Unauthorized
 *       403:
 *         description: Forbidden (not admin)
 */
router.post("/", auth, isAdmin, validateFields([
  "username",
  "password",
  "fullName",
  "role",
  "email",
  "phoneNumber",
  "address",
  "gender",
  "dateOfBirth",
]), createUser);

router.post("/login", validateFields(["username", "password"]), login);
router.get("/me", auth, getMe);
router.put("/me", auth, updateUser);
router.put("/:userId", auth, isAdmin, updateUserByAdmin);
router.delete("/:id", auth, isAdmin, deleteUser);
router.post("/filter", auth, isAdmin, filterUsers);
router.get("/", auth, isAdmin, getUsers);

module.exports = router;
