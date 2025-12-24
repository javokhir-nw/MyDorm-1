package javier.com.mydorm1.dto;

import javier.com.mydorm1.auth.model.User;
import javier.com.mydorm1.model.Dormitory;
import javier.com.mydorm1.repo.Floor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FloorResponseDto {
    private Long id;
    private String name;
    private Long leaderId;
    private String leaderFirstName;
    private String leaderLastName;
    private String leaderMiddleName;
    private Long dormitoryId;
    private String dormitoryName;
    private List<RoomResponseDto> rooms;
    public FloorResponseDto(Floor floor) {
        id =  floor.getId();
        name =  floor.getName();
        User leader = floor.getLeader();
        if (leader != null) {
            leaderId = leader.getId();
            leaderFirstName = leader.getFirstName();
            leaderLastName = leader.getLastName();
            leaderMiddleName = leader.getMiddleName();
        }
        Dormitory dorm = floor.getDormitory();
        if (dorm != null) {
            dormitoryId = dorm.getId();
            dormitoryName = dorm.getName();
        }
    }
}
