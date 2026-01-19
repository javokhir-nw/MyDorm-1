package javier.com.mydorm1.dto;

import javier.com.mydorm1.auth.dto.UserDto;
import javier.com.mydorm1.auth.model.User;
import javier.com.mydorm1.model.Dormitory;
import javier.com.mydorm1.model.Floor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FloorResponseDto {
    private Long id;
    private String name;
    private List<UserDto> userDtos = new ArrayList<>();
    private Long dormitoryId;
    private String dormitoryName;
    private String randString;
    private List<RoomResponseDto> rooms;
    public FloorResponseDto(Floor floor) {
        id =  floor.getId();
        name =  floor.getName();
        randString = floor.getFloorTelegramIdentity();
        Dormitory dorm = floor.getDormitory();
        if (dorm != null) {
            dormitoryId = dorm.getId();
            dormitoryName = dorm.getName();
        }
    }

    public FloorResponseDto(Floor f, List<User> leaders) {
        this(f);
        if (leaders != null &&  !leaders.isEmpty()){
            userDtos = leaders.stream().map(UserDto::new).toList();
        }
    }
}
