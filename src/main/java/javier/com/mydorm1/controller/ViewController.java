package javier.com.mydorm1.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping()
public class ViewController {
    @PreAuthorize("hasAnyAuthority(" +
            "'view dormitories'," +
            "'view users'," +
            "'view room-types'," +
            "'view duties'," +
            "'view universities'," +
            "'view roles'," +
            "'view attendances'" +
            ")")
    public void createPermission() {
    }
}