package com.example.movie_ticket_.controller.api;

import com.example.movie_ticket_.entity.User;
import com.example.movie_ticket_.entity.UserDeviceToken;
import com.example.movie_ticket_.repository.UserDeviceTokenRepository;
import com.example.movie_ticket_.repository.UserRepository;
import com.example.movie_ticket_.security.JwtUtil;
import com.example.movie_ticket_.service.EmailService;
import com.example.movie_ticket_.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final UserDeviceTokenRepository userDeviceTokenRepository;

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> request) {
        String idToken = request.get("idToken");

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String email = decodedToken.getEmail();
            String name = decodedToken.getName();

            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        String rawPassword = userService.generateRandomPassword(10);
                        String encodedPassword = userService.encodePassword(rawPassword);

                        User newUser = new User();
                        newUser.setEmail(email);
                        newUser.setName(name);
                        newUser.setPassword(encodedPassword);
                        newUser.setPoint(0);
                        newUser.setTotalSpent(0L);
                        newUser.setStatus(1);
                        newUser.setRole(0);

                        User savedUser = userRepository.save(newUser);
                        emailService.sendRandomPassword(email, rawPassword);
                        return savedUser;
                    });

            // ✅ Trả JWT + user
            String token = jwtUtil.generateToken(user.getEmail());
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "user", user
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }



    //login email password

    @PostMapping("/login")
    public ResponseEntity<?> loginWithEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        return userRepository.findByEmail(email)
                .map(user -> {
                    if (userService.matchesPassword(password, user.getPassword())) {
                        // ✅ Sinh JWT
                        String token = jwtUtil.generateToken(user.getEmail());
                        return ResponseEntity.ok(Map.of(
                                "token", token,
                                "user", user
                        ));
                    } else {
                        return ResponseEntity.badRequest().body(Map.of("error", "Email hoặc mật khẩu không chính xác!"));
                    }
                })
                .orElseGet(() -> ResponseEntity.badRequest().body(Map.of("error", "Email không tồn tại!")));
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(value = "Device-Id") String deviceId) {

        userDeviceTokenRepository.deleteByDeviceId(deviceId);
        return ResponseEntity.ok("Log out OK");
    }


    @PostMapping("/save-fcm-token")
    public ResponseEntity<?> saveFcm(
            @RequestHeader("Authorization") String jwt,
            @RequestBody Map<String, String> body) {

        String token = jwt.substring(7);
        String email = jwtUtil.extractSubject(token);

        User user = userRepository.findByEmail(email).orElseThrow();

        String fcmToken = body.get("fcmToken");
        String deviceId = body.get("deviceId");

        userDeviceTokenRepository.deleteByDeviceId(deviceId);

        UserDeviceToken udt = new UserDeviceToken();
        udt.setUser(user);
        udt.setDeviceId(deviceId);
        udt.setFcmToken(fcmToken);
        userDeviceTokenRepository.save(udt);

        return ResponseEntity.ok("Saved");
    }

}
