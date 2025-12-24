package javier.com.mydorm1.auth.dto;

import jakarta.persistence.*;
import javier.com.mydorm1.auth.model.Permission;
import javier.com.mydorm1.auth.model.Role;
import javier.com.mydorm1.auth.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String username;
    private String password;
    private String telegramUsername;
    private String phone;
    private Boolean enabled = FALSE;
    private String token;
    private List<String> roles = new ArrayList<>();
    private List<Long> roleIds = new ArrayList<>();
    private List<Permission> permissions = new ArrayList<>();
    private Status status;
}
