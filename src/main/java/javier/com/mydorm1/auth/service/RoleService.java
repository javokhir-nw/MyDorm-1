package javier.com.mydorm1.auth.service;

import jakarta.persistence.EntityNotFoundException;
import javier.com.mydorm1.auth.dto.RoleDto;
import javier.com.mydorm1.auth.model.Permission;
import javier.com.mydorm1.auth.model.Role;
import javier.com.mydorm1.auth.repo.PermissionRepository;
import javier.com.mydorm1.auth.repo.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public Role getById(Long roleId){
       return roleRepository.findById(roleId).orElseThrow(() -> new EntityNotFoundException("Role is not found by id " + roleId));
    }

    public List<RoleDto> getList() {
        return roleRepository.findAll().stream().filter(r -> r.getCode() != null && !r.getCode().equals("ROLE_ADMIN")).map(RoleDto::new).toList();
    }

    public String createOrUpdate(RoleDto dto) {
        Long roleId = dto.getId();
        Role role;
        if (roleId != null) {
            role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("NOT_EXISTS"));
        } else {
            role = new Role();
        }
        role.setName(dto.getName());
        role.setCode(dto.getCode());
        List<Long> permissionIds = dto.getPermissionIds();
        if (permissionIds != null && !permissionIds.isEmpty()){
            role.setPermissions(new HashSet<>(permissionRepository.findAllById(permissionIds)));
        }
        roleRepository.save(role);
        return "SUCCESS_SAVED";
    }

    public RoleDto getRoleById(Long roleId){
        Role role = getById(roleId);
        RoleDto dto = new RoleDto(role);
        dto.setPermissionIds(role.getPermissions().stream().map(Permission::getId).toList());
        return dto;
    }
}
