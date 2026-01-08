package javier.com.mydorm1.auth.controller;

import javier.com.mydorm1.auth.dto.RoleDto;
import javier.com.mydorm1.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/role")
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('get role-list')")
    public ResponseEntity<List<RoleDto>> getList(){
        return ResponseEntity.ok(roleService.getList());
    }
}
