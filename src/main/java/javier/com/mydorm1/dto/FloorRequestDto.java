package javier.com.mydorm1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FloorRequestDto {
    private Long id;
    private String name;
    private Long leaderId;
    private Long dormId;
    private String randomString;
}
