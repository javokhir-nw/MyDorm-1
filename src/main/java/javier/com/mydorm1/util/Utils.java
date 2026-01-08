package javier.com.mydorm1.util;

import javier.com.mydorm1.auth.model.Role;
import javier.com.mydorm1.auth.model.User;
import javier.com.mydorm1.auth.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Utils {

    @Value("${initial.role-admin.code}")
    private String roleAdminCode;

    private final UserRepository userRepository;
    public User getCurrentUser() {
        return userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }
    public Boolean isAdmin(){
        return getCurrentUser().getRoles().stream().map(Role::getCode).toList().contains(roleAdminCode);
    }
}
