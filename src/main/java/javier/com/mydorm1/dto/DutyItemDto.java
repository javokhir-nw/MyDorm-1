package javier.com.mydorm1.dto;

import javier.com.mydorm1.auth.dto.UserDto;
import javier.com.mydorm1.model.DutyItem;
import javier.com.mydorm1.model.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DutyItemDto {

    private Long id;
    private Long roomId;
    private String roomName;
    private String roomNumber;
    private List<UserDto> dutyUsers;

    public DutyItemDto(DutyItem di) {
        id = di.getId();
        Room room = di.getRoom();
        if (room != null){
            roomId = room.getId();
            roomName = room.getName();
            roomNumber = room.getNumber();
        }
    }
}
