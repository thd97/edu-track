package com.example.frontend;

import com.google.gson.Gson;
import com.example.frontend.Account;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiService {
    public static Account getCurrentAccount() {
        try {
            URL url = new URL(ApiConstants.GET_USERS_API);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + LoginController.userToken);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            Gson gson = new Gson();
            return gson.fromJson(response.toString(), Account.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

