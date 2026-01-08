package javier.com.mydorm1.auth.service;

import jakarta.persistence.EntityNotFoundException;
import javier.com.mydorm1.auth.dto.RoleDto;
import javier.com.mydorm1.auth.model.Role;
import javier.com.mydorm1.auth.repo.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getById(Long roleId){
       return roleRepository.findById(roleId).orElseThrow(() -> new EntityNotFoundException("Role is not found by id " + roleId));
    }

    public List<RoleDto> getList() {
        return roleRepository.findAll().stream().map(RoleDto::new).toList();
    }
}
