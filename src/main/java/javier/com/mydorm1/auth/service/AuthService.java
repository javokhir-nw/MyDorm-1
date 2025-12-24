package javier.com.mydorm1.auth.service;

import javier.com.mydorm1.auth.dto.AuthRequest;
import javier.com.mydorm1.auth.dto.UserDto;
import javier.com.mydorm1.auth.dto.UserRequestDto;
import javier.com.mydorm1.auth.jwt.JwtService;
import javier.com.mydorm1.auth.mapper.UserMapper;
import javier.com.mydorm1.auth.model.User;
import javier.com.mydorm1.auth.repo.RoleRepository;
import javier.com.mydorm1.auth.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    @Value("${initial.role-user.code}")
    private String roleUserCode;
    private final AuthenticationManager authenticationManager;

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;

    public UserDto register(UserRequestDto userDto) {
        String username = userDto.getUsername();
        if(userRepository.existByUsername(null,username)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username is already in use");
        }
        User orUpdateUser = userMapper.createOrUpdateUser(userDto, null);
        orUpdateUser.getRoles().add(roleRepository.findByCode(roleUserCode));
        orUpdateUser = userRepository.save(orUpdateUser);
        UserDto dto = userMapper.toDto(orUpdateUser);
        dto.setToken(jwtService.generateToken(orUpdateUser));
        return dto;
    }

    public UserDto login(AuthRequest userDto) {
        String username = userDto.getUsername();
        String password = userDto.getPassword();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        User user = userRepository.findByUsername(username);
        UserDto dto = userMapper.toDto(user);
        dto.setToken(jwtService.generateToken(user));
        return dto;
    }
}
