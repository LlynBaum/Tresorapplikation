package ch.bbw.pr.tresorbackend.controller;

import ch.bbw.pr.tresorbackend.model.LoginRequest;
import ch.bbw.pr.tresorbackend.model.RegisterUser;
import ch.bbw.pr.tresorbackend.model.User;
import ch.bbw.pr.tresorbackend.service.PasswordEncryptionService;
import ch.bbw.pr.tresorbackend.service.RecaptchaService;
import ch.bbw.pr.tresorbackend.service.UserService;
import ch.bbw.pr.tresorbackend.util.JwtUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final PasswordEncryptionService passwordService;
    private final RecaptchaService recaptchaService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserService userService, PasswordEncryptionService passwordService, RecaptchaService recaptchaService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordService = passwordService;
        this.recaptchaService = recaptchaService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterUser registerUser, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                    .toList();
            JsonArray arr = new JsonArray();
            errors.forEach(arr::add);
            JsonObject obj = new JsonObject();
            obj.add("message", arr);
            return ResponseEntity.badRequest().body(new Gson().toJson(obj));
        }
        if (!recaptchaService.verifyRecaptcha(registerUser.getRecaptchaToken())) {
            JsonObject obj = new JsonObject();
            obj.addProperty("message", "Recaptcha verification failed.");
            return ResponseEntity.badRequest().body(new Gson().toJson(obj));
        }
        String password = registerUser.getPassword();
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!/%*?&])[A-Za-z\\d@$!%/*?&]{8,}$";
        if (!Pattern.matches(passwordRegex, password)) {
            JsonObject obj = new JsonObject();
            obj.addProperty("message", "Password must be at least 8 characters long, include one uppercase letter, one number, and one special character.");
            return ResponseEntity.badRequest().body(new Gson().toJson(obj));
        }

        User user = new User(
                null,
                registerUser.getFirstName(),
                registerUser.getLastName(),
                registerUser.getEmail(),
                passwordService.hashPassword(registerUser.getPassword()),
                "USER"
        );
        userService.createUser(user);
        JsonObject obj = new JsonObject();
        obj.addProperty("answer", "User Saved");
        return ResponseEntity.accepted().body(new Gson().toJson(obj));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        User user = userService.findByEmail(loginRequest.getEmail());
        if (user == null || !passwordService.checkPassword(loginRequest.getPassword(), user.getPassword())) {
            JsonObject obj = new JsonObject();
            obj.addProperty("message", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Gson().toJson(obj));
        }
        String jwt = jwtUtil.generateToken(user.getId(), user.getEmail(), List.of(user.getRole()));
        Cookie jwtCookie = new Cookie("jwt", jwt);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60 * 60);
        response.addCookie(jwtCookie);
        JsonObject obj = new JsonObject();
        obj.addProperty("message", "Login successful");
        return ResponseEntity.ok(new Gson().toJson(obj));
    }
}
