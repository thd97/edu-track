package com.example.frontend;

public class ApiConstants {

    public static final String BASE_URL = "http://localhost:3004";

    // Accounts APIs
    public static final String LOGIN_API = BASE_URL + "/api/users/login";
    public static final String GET_USERS_API = BASE_URL + "/api/users";
    public static final String CREATE_ACCOUNT_API = BASE_URL + "/api/users";
    public static final String UPDATE_ACCOUNT_API = BASE_URL + "/api/users/:id";
    public static final String DELETE_ACCOUNT_API = BASE_URL + "/api/users/:id";

    // Class APIs
    public static final String GET_CLASSES_API = BASE_URL + "/api/classes";
    public static final String CREATE_CLASS_API = BASE_URL + "/api/classes";
    public static final String UPDATE_CLASS_API = BASE_URL + "/api/classes/:id";
    public static final String DELETE_CLASS_API = BASE_URL + "/api/classes/:id";

    // Students APIs
    public static final String GET_STUDENTS_API = BASE_URL + "/api/students";
    public static final String CREATE_STUDENT_API = BASE_URL + "/api/students";
    public static final String UPDATE_STUDENT_API = BASE_URL + "/api/students/:id";
    public static final String DELETE_STUDENT_API = BASE_URL + "/api/students/:id";
}
