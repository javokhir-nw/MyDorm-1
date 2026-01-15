package javier.com.mydorm1.util;

import javier.com.mydorm1.auth.model.Role;
import javier.com.mydorm1.auth.model.User;
import javier.com.mydorm1.auth.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Utils {

    @Value("${initial.role-admin.code}")
    private String roleAdminCode;

    @Value("${initial.role-captain.code}")
    private String roleCaptain;

    private final UserRepository userRepository;
    public User getCurrentUser() {
        return userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }
    public Boolean isAdmin(){
        return getCurrentUser().getRoles().stream().map(Role::getCode).toList().contains(roleAdminCode);
    }

    public String formatDateDDMMYYYY(Date date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        LocalDate localDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        return localDate.format(formatter);
    }

    public Boolean isCaptain(User user){
        return user!= null && user.getRoles().stream().map(Role::getCode).toList().contains(roleCaptain);
    }

    public String createMarkdownMention(String name,String userId){
        return String.format("[%s](tg://user?id=%s)", name, userId);
    }

    public Set<Long> extractIdsFromString(String text){
        if (text != null && !text.isEmpty()){
            return Arrays.stream(text.split(",")).map(Long::valueOf).collect(Collectors.toSet());
        } else {
            return new LinkedHashSet<>();
        }
    }

    public String getRandomString(){
        return UUID.randomUUID().toString().replace("-","");
    }

    public Date test() {
       return userRepository.selectNow();
    }
}
