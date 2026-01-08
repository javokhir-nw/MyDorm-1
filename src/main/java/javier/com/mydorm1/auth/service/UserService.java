package javier.com.mydorm1.auth.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import javier.com.mydorm1.auth.dto.UserDto;
import javier.com.mydorm1.auth.dto.UserRequestDto;
import javier.com.mydorm1.auth.mapper.UserMapper;
import javier.com.mydorm1.auth.model.Role;
import javier.com.mydorm1.auth.model.Status;
import javier.com.mydorm1.auth.model.User;
import javier.com.mydorm1.auth.repo.RoleRepository;
import javier.com.mydorm1.auth.repo.UserRepository;
import javier.com.mydorm1.dto.PageWrapper;
import javier.com.mydorm1.dto.Pagination;
import javier.com.mydorm1.dto.Search;
import javier.com.mydorm1.repo.DormitoryRepository;
import javier.com.mydorm1.service.DormitoryService;
import javier.com.mydorm1.service.FloorService;
import javier.com.mydorm1.service.RoomService;
import javier.com.mydorm1.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final DormitoryService dormitoryService;
    private final FloorService floorService;
    private final RoomService roomService;
    private final RoleService roleService;
    @Value("${initial.role-admin.code}")
    private String roleAdminCode;


    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final Utils utils;


    public String createOrUpdate(UserRequestDto dto, User user) {
        user = userMapper.createOrUpdateUser(dto, user);
        Long roleId = dto.getRoleId();
        if (roleId != null){
            Role role = roleService.getById(roleId);
            if (role.getCode().equals(roleAdminCode)){
                if (utils.isAdmin()){
                    user.getRoles().add(role);
                }
            } else {
                user.getRoles().add(role);
            }
        }
        Long dormId = dto.getDormId();
        if (dormId != null){
            user.setDormitory(dormitoryService.findEntityById(dormId));
        }
        Long floorId = dto.getFloorId();
        if (floorId != null){
            user.setFloor(floorService.getEntityById(floorId));
        }
        Long roomId = dto.getRoomId();
        if (roomId != null){
            user.setRoom(roomService.findEntityById(roomId));
        }
        userRepository.save(user);
        return "SUCCESS_SAVED";
    }

    @SneakyThrows
    public String update(UserRequestDto dto) {
        Long userId = dto.getId();
        if (userId == null){
            throw new BadRequestException("UserId is null");
        }
        createOrUpdate(dto,findEntityById(userId));
        return "SUCCESS_UPDATED";
    }

    public User findEntityById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User is not found by id " + id));
    }

    public PageWrapper getList(Pagination<Search> pagination) {
        Search search = pagination.getSearch();
        String value = search.getValue();
        Page<User> users = userRepository.findAllByPagination(value,search.getDormId(), search.getFloorId(),search.getRoomId(), PageRequest.of(pagination.getPage(),pagination.getSize()));
        List<UserDto> list = users.stream().map(u -> userMapper.toDto(u, Boolean.TRUE)).toList();
        return PageWrapper.builder().total(users.getTotalElements()).totalPages(users.getTotalPages()).list(list).build();
    }

    @Transactional
    public String deleteUser(Long id) {
        userRepository.changeUserStatus(id, Status.DELETED);
        return "SUCCESS_DELETED";
    }
}
