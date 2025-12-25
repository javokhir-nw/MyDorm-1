package javier.com.mydorm1.auth.controller;

import javier.com.mydorm1.auth.dto.AuthRequest;
import javier.com.mydorm1.auth.dto.UserDto;
import javier.com.mydorm1.auth.dto.UserRequestDto;
import javier.com.mydorm1.auth.service.AuthService;
import javier.com.mydorm1.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserRequestDto userDto) {
        return ResponseEntity.ok(authService.register(userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/check-exist-username")
    public ResponseEntity<Boolean> checkExistUsername(@RequestParam String username) {
        return ResponseEntity.ok(authService.checkExitUsername(username));
    }
}
