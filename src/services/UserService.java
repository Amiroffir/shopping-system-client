package services;

import models.User;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class UserService {
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String login(String email, String password) {
        String userType = null;
        User loggedInUser;
        User currentUser = new User(email, password);

        try {
            HttpRequest request =
                    ServicesManager.getInstance().getHttpService().createRequest("/users/login", "POST", false);
            request = ServicesManager.getInstance().getHttpService().setRequestToOutput(request, currentUser, "POST");

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                if (ServicesManager.getInstance().getSessionToken() == null) {
                    // Get session ID from the response header
                    ServicesManager.getInstance().setSessionToken(response.headers().firstValue("Set-Cookie").get());
                }
            }

            loggedInUser = ServicesManager.getInstance().getHttpService()
                    .parseResponse(response, User.class, HttpURLConnection.HTTP_OK);

            userType = loggedInUser.getUserType();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return userType;
    }


    public String register(String name, String email, String password) {
        User loggedInUser;
        String userType = null;
        User newUser = new User(name, email, password);
        try {
            HttpRequest request =
                    ServicesManager.getInstance().getHttpService().createRequest("/users/register", "POST", false);
            request = ServicesManager.getInstance().getHttpService().setRequestToOutput(request, newUser, "POST");

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                if (ServicesManager.getInstance().getSessionToken() == null) {
                    // Get session ID from the response header
                    ServicesManager.getInstance().setSessionToken(response.headers().firstValue("Set-Cookie").get());
                }
            }

            loggedInUser = ServicesManager.getInstance().getHttpService()
                    .parseResponse(response, User.class, HttpURLConnection.HTTP_OK);

            userType = loggedInUser.getUserType();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return userType;
    }

    public List<User> getAllUsers() {
        List<User> users;
        try {
            HttpRequest request =
                    ServicesManager.getInstance().getHttpService().createRequest("/users", "GET", false);

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            users = ServicesManager.getInstance().getHttpService()
                    .parseResponseToList(response, User.class, HttpURLConnection.HTTP_OK);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return users;
    }
}