package javier.com.mydorm1.auth.controller;

import javier.com.mydorm1.auth.repo.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionRepository permissionRepository;

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('get permissions-list')")
    public ResponseEntity<?> getPermissionsList(){
        return ResponseEntity.ok(permissionRepository.findAll());
    }
}
