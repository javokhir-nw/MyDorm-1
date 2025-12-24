package javier.com.mydorm1.dto;

import javier.com.mydorm1.model.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoomTypeDto {
    private Long id;
    private String name;

    public RoomTypeDto(RoomType roomType) {
        id =  roomType.getId();
        name = roomType.getName();
    }
}
