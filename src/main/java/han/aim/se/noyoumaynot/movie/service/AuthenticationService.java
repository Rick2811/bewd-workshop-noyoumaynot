package han.aim.se.noyoumaynot.movie.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class AuthenticationService {
  private static final String HARDCODED_USERNAME = "admin";
  private static final String HARDCODED_PASSWORD = "password123";
  private ArrayList<UserToken> userTokens = new ArrayList<>();

  // Login method which generates a token if the username and password are correct
  public UserToken login(String username, String password) {
    if (HARDCODED_USERNAME.equals(username) && HARDCODED_PASSWORD.equals(password)) {
      String token = UUID.randomUUID().toString(); // Generate a unique token
      UserToken userToken = new UserToken(username, token);
      userTokens.add(userToken); // Save the generated token
      return userToken;
    }
    return null; // Return null if login credentials are invalid
  }

  // Checks if the provided token is valid
  public boolean isValidToken(String token) {
    return userTokens.stream().anyMatch(userToken -> userToken.getToken().equals(token));
  }

  // Retrieves the username associated with the provided token
  public String getUsername(String token) {
    return userTokens.stream()
            .filter(userToken -> userToken.getToken().equals(token))
            .map(UserToken::getUsername)
            .findFirst()
            .orElse(null);
  }

  // Inner class to represent the user token (this replaces the external UserToken class)
  public class UserToken {
    private String username;
    private String token;

    public UserToken(String username, String token) {
      this.username = username;
      this.token = token;
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getToken() {
      return token;
    }

    public void setToken(String token) {
      this.token = token;
    }
  }
}
