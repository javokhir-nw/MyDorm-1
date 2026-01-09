package javier.com.mydorm1.auth.mapper;

import javier.com.mydorm1.auth.dto.UserDto;
import javier.com.mydorm1.auth.dto.UserRequestDto;
import javier.com.mydorm1.auth.model.Role;
import javier.com.mydorm1.auth.model.User;
import javier.com.mydorm1.model.Dormitory;
import javier.com.mydorm1.model.Room;
import javier.com.mydorm1.model.Floor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User createOrUpdateUser(UserRequestDto dto, User user) {
        if (user == null) {
            user = new User();
        }
        String firstName = dto.getFirstName();
        if (firstName != null) {
            user.setFirstName(firstName);
        }
        String lastName = dto.getLastName();
        if (lastName != null) {
            user.setLastName(lastName);
        }
        String middleName = dto.getMiddleName();
        if (middleName != null) {
            user.setMiddleName(middleName);
        }
        String username = dto.getUsername();
        if (username != null) {
            user.setUsername(username);
        }
        String password = dto.getPassword();
        if (password != null) {
            user.setPassword(passwordEncoder.encode(password));
        }
        String telegramUsername = dto.getTelegramUsername();
        if (telegramUsername != null) {
            user.setTelegramUsername(telegramUsername);
        }
        String phone = dto.getPhone();
        if (phone != null) {
            user.setPhone(phone);
        }
        user.setEnabled(Boolean.TRUE);
        return user;
    }

    public UserDto toDto(User user, Boolean forList) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setMiddleName(user.getMiddleName());
        userDto.setPhone(user.getPhone());

        Dormitory dormitory = user.getDormitory();
        if (dormitory != null) {
            userDto.setDormId(dormitory.getId());
            userDto.setDormName(dormitory.getName());
        }
        Floor floor = user.getFloor();
        if (floor != null) {
            userDto.setFloorId(floor.getId());
            userDto.setFloorNumber(floor.getName());
        }
        Room room = user.getRoom();
        if (room != null) {
            userDto.setRoomId(room.getId());
            userDto.setRoomNumber(room.getNumber());
        }

        Set<Role> roles = user.getRoles();
        if (roles != null && !roles.isEmpty()) {
            userDto.setRoles(roles.stream().map(Role::getName).toList());
            userDto.setRoleIds(roles.stream().map(Role::getId).toList());
            if (!forList) {
                userDto.setPermissions(roles.stream()
                        .flatMap(r -> r.getPermissions().stream()).toList());
            }
        }
        userDto.setUsername(user.getUsername());
        userDto.setTelegramUsername(user.getTelegramUsername());
        userDto.setEnabled(user.getEnabled());
        userDto.setStatus(user.getStatus());

        return userDto;
    }
}
