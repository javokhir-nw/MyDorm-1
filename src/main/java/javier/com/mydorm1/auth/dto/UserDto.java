package javier.com.mydorm1.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import javier.com.mydorm1.auth.model.Permission;
import javier.com.mydorm1.auth.model.Role;
import javier.com.mydorm1.auth.model.Status;
import javier.com.mydorm1.auth.model.User;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String username;
    private String password;
    private String telegramUsername;
    private String phone;
    private Boolean enabled;
    private String token;
    private List<String> roles;
    private List<Long> roleIds;
    private List<Permission> permissions;
    private Status status;
    private Long dormId;
    private String dormName;
    private Long floorId;
    private String floorNumber;
    private Long roomId;
    private String roomNumber;

    private Boolean isAttended;
}
