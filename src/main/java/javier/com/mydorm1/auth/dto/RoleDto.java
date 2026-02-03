package javier.com.mydorm1.auth.dto;

import javier.com.mydorm1.auth.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleDto {
    private Long id;
    private String name;
    private String code;
    private List<Long> permissionIds;

    public RoleDto(Role role) {
        id = role.getId();
        name = role.getName();
        code = role.getCode();
    }
}
