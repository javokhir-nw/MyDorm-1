package javier.com.mydorm1.dto;

import javier.com.mydorm1.model.Room;
import javier.com.mydorm1.model.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoomResponseDto {
    private Long id;
    private String name;
    private String number;
    private Integer capacity = 0;
    private Long roomTypeId;
    private String roomTypeName;
    private Boolean isRoom = Boolean.TRUE;

    public RoomResponseDto(Room room) {
        id = room.getId();
        name = room.getName();
        number = room.getNumber();
        capacity = room.getCapacity();
        isRoom = room.getIsRoom();
        RoomType roomType = room.getRoomType();
        if (roomType != null) {
            roomTypeId = roomType.getId();
            roomTypeName = roomType.getName();
        }
    }
}
