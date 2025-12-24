package javier.com.mydorm1.util;

import javier.com.mydorm1.auth.model.User;
import javier.com.mydorm1.auth.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Utils {
    private final UserRepository userRepository;
    public User getCurrentUser() {
        return userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
