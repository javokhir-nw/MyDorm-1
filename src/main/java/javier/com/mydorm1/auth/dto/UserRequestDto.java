package javier.com.mydorm1.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRequestDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String username;
    private String password;
    private String telegramUsername;
    private String phone;

}
