const express = require("express");
const router = express.Router();
const userController = require("../controllers/userController");
const { auth, isAdmin } = require("../middleware/auth");
const { validateFields } = require("../utils/validator");

router.post(
  "/",
  auth,
  isAdmin,
  validateFields([
    "username",
    "password",
    "fullName",
    "role",
    "email",
    "phoneNumber",
    "address",
    "gender",
    "dateOfBirth",
  ]),
  userController.createUser
);

router.post(
  "/login",
  validateFields(["username", "password"]),
  userController.login
);
router.get("/me", auth, userController.getMe);
router.put("/me", auth, userController.updateUser);
router.put("/:userId", auth, isAdmin, userController.updateUserByAdmin);
router.delete("/:id", auth, isAdmin, userController.deleteUser);
router.post("/filter", auth, isAdmin, userController.filterUsers);
router.get("/", auth, isAdmin, userController.getUsers);

module.exports = router;
