package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;

public class UserService {

    public String login(String email, String password) {
        String userType = null;
        User loggedInUser;
        User currentUser = new User(email, password);
        HttpURLConnection connection = null;
        try {
            connection = ServicesManager.getInstance().createConnection("/users/login", "POST", true);
            try (OutputStream os = connection.getOutputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(os, currentUser);
            }

            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                if (ServicesManager.getInstance().getSessionToken() == null) {
                    // Get session ID from the response header
                    ServicesManager.getInstance().setSessionToken(connection.getHeaderField("Set-Cookie"));
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                ObjectMapper mapper = new ObjectMapper();
                loggedInUser = mapper.readValue(reader, User.class);
                userType = loggedInUser.getUserType();
            } else {
                throw new RuntimeException(connection.getResponseMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect(); // Disconnect and close the connection
        }
        return userType;
    }

    public String register(String name, String email, String password) {
        User loggedInUser;
        String userType = null;
        User newUser = new User(name, email, password);
        HttpURLConnection connection = null;
        try {
            connection = ServicesManager.getInstance().createConnection("/users/register", "POST",true);
            try (OutputStream os = connection.getOutputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(os, newUser);
            }

            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                ObjectMapper mapper = new ObjectMapper();
                loggedInUser = mapper.readValue(reader, User.class);
                userType = loggedInUser.getUserType();

                if (ServicesManager.getInstance().getSessionToken() == null) {
                    ServicesManager.getInstance().setSessionToken(connection.getHeaderField("Set-Cookie")); // Get
                    // session ID from the response header
                }
            } else {
                throw new RuntimeException(connection.getResponseMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect(); // Disconnect and close the connection
        }
        return userType;
    }

    public List<User> getAllUsers() {
        List<User> users;
        HttpURLConnection connection = null;
        try {
            connection = ServicesManager.getInstance().createConnection("/users", "GET",false);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                ObjectMapper mapper = new ObjectMapper();
                users = mapper.readValue(reader, mapper.getTypeFactory().constructCollectionType(List.class, User.class));
            } else {
                throw new RuntimeException(connection.getResponseMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect(); // Disconnect and close the connection
        }
        return users;
    }
}