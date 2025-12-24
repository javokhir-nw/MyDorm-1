package javier.com.mydorm1.auth.config;

import javier.com.mydorm1.auth.model.User;
import javier.com.mydorm1.auth.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User byUsername = userRepository.findByUsernameEager(username);
        if (byUsername == null) {
            throw new UsernameNotFoundException(username);
        }
        return byUsername;
    }
}
