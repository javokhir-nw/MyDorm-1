package javier.com.mydorm1.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import javier.com.mydorm1.auth.dto.UserDto;
import javier.com.mydorm1.model.Attendance;
import javier.com.mydorm1.model.Floor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttendanceDto {
    private Long id;
    private String dormName;
    private String floorName;
    @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Tashkent")
    private Date createdDate;
    private List<UserDto> userDtoList;
    public AttendanceDto(Attendance attendance) {
        id = attendance.getId();
        Floor floor = attendance.getFloor();
        if (floor != null){
            floorName = floor.getName();
            dormName = floor.getDormitory().getName();
        }
        createdDate = attendance.getCreatedDate();
    }
}
