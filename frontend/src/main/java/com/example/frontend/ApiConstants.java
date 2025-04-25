package com.example.frontend;

public class ApiConstants {
    public static final String BASE_URL = "http://localhost:3004";
    public static final String LOGIN_API = BASE_URL + "/api/users/login";
    public static final String GET_USERS_API = BASE_URL + "/api/users";
    public static final String CREATE_ACCOUNT_API = BASE_URL + "/api/users";
    public static final String UPDATE_ACCOUNT_API = BASE_URL + "/api/users/:id";
    public static final String DELETE_ACCOUNT_API = BASE_URL + "/api/users/:id";
}
