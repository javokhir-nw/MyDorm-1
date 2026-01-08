package javier.com.mydorm1.auth.controller;

import javier.com.mydorm1.auth.dto.UserRequestDto;
import javier.com.mydorm1.auth.model.User;
import javier.com.mydorm1.auth.service.UserService;
import javier.com.mydorm1.dto.PageWrapper;
import javier.com.mydorm1.dto.Pagination;
import javier.com.mydorm1.dto.Search;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('create user')")
    public ResponseEntity<String> createUser(@RequestBody UserRequestDto dto){
        return ResponseEntity.ok(userService.createOrUpdate(dto,new User()));
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('update user')")
    public ResponseEntity<String> updateUser(@RequestBody UserRequestDto dto){
        return ResponseEntity.ok(userService.update(dto));
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('list user')")
    public ResponseEntity<PageWrapper> getUserList(@RequestBody Pagination<Search> pagination){
        return ResponseEntity.ok(userService.getList(pagination));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('delete user')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        return ResponseEntity.ok(userService.deleteUser(id));
    }
}
