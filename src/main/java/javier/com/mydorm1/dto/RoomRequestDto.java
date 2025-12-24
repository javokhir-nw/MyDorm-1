package javier.com.mydorm1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static java.lang.Boolean.TRUE;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoomRequestDto {
    private Long id;
    private String number;
    private String name;
    private Long floorId;
    private Boolean isRoom = TRUE;
}
