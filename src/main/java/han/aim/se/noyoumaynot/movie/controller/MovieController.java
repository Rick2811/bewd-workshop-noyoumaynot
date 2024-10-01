package han.aim.se.noyoumaynot.movie.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Movie domain class
class Movie {
    private String id;
    private String title;
    private String genre;

    public Movie(String id, String title, String genre) {
        this.id = id;
        this.title = title;
        this.genre = genre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}

// UserToken class to represent a user's token
class UserToken {
    private String username;
    private String token;

    public UserToken(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }
}

// Controller class
@RestController
@RequestMapping("/movies")
public class MovieController {

    private final AuthenticationService authenticationService;
    private String currentToken = null; // Token storage

    // Hardcoded username and password for login
    private static final String HARDCODED_USERNAME = "admin";
    private static final String HARDCODED_PASSWORD = "password123";

    // In-memory list to store movies
    private List<Movie> movieList = new ArrayList<>();

    @Autowired
    public MovieController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    // Login method at /login URL
    @GetMapping("/login")
    public ResponseEntity<String> login() {
        UserToken userToken = authenticationService.login(HARDCODED_USERNAME, HARDCODED_PASSWORD);

        if (userToken != null) {
            currentToken = userToken.getToken(); // Store the generated token
            return ResponseEntity.ok("Logged in successfully! Token: " + currentToken);
        } else {
            return ResponseEntity.status(401).body("Invalid login credentials");
        }
    }

    @GetMapping
    public List<Movie> getAllMovies() throws Exception {
        authenticate(); // Check token at each endpoint
        return movieList;
    }

    @GetMapping("/show")
    public Movie getMovieById(@RequestParam("id") String id) throws Exception {
        authenticate(); // Check token
        Optional<Movie> movie = movieList.stream().filter(m -> m.getId().equals(id)).findFirst();
        return movie.orElse(null); // Return the movie or null if not found
    }

    @PostMapping("/add")
    public ResponseEntity<Movie> addMovie(@RequestBody Movie movie) throws Exception {
        authenticate(); // Check token
        movieList.add(movie);
        return ResponseEntity.ok(movie);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMovie(@PathVariable("id") String id) throws Exception {
        authenticate(); // Check token
        movieList.removeIf(movie -> movie.getId().equals(id));
        return ResponseEntity.ok("Movie deleted successfully");
    }

    // Authenticate the token using the AuthenticationService
    private void authenticate() throws Exception {
        if (currentToken == null || !authenticationService.isValidToken(currentToken)) {
            throw new AuthenticationException("Invalid or missing token. Please login first.");
        }
    }

    // Authentication service
    @Service
    public static class AuthenticationService {
        private ArrayList<UserToken> userTokens = new ArrayList<>();

        public UserToken login(String username, String password) {
            if (HARDCODED_USERNAME.equals(username) && HARDCODED_PASSWORD.equals(password)) {
                String token = UUID.randomUUID().toString(); // Generate a unique token
                UserToken userToken = new UserToken(username, token);
                userTokens.add(userToken); // Save the generated token
                return userToken;
            }
            return null; // Return null if login credentials are invalid
        }

        public boolean isValidToken(String token) {
            return userTokens.stream().anyMatch(userToken -> userToken.getToken().equals(token));
        }

        public String getUsername(String token) {
            return userTokens.stream()
                    .filter(userToken -> userToken.getToken().equals(token))
                    .map(UserToken::getUsername)
                    .findFirst()
                    .orElse(null);
        }
    }
}
