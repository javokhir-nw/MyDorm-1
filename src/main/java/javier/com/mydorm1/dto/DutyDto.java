package javier.com.mydorm1.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import javier.com.mydorm1.auth.model.User;
import javier.com.mydorm1.model.Dormitory;
import javier.com.mydorm1.model.Duty;
import javier.com.mydorm1.model.Floor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DutyDto {
    private Long id;

    private Long dormitoryId;
    private String dormitoryName;

    private Long floorId;
    private String floorName;

    private Long creatorId;
    private String creatorFirstName;
    private String creatorLastName;
    private String creatorMiddleName;

    @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Tashkent")
    private Date createdDate;

    private List<DutyItemDto> dutyItemList;

    public DutyDto(Duty duty){
        id = duty.getId();
        createdDate = duty.getCreatedDate();
        Floor floor = duty.getFloor();
        if (floor != null){
            floorId = floor.getId();
            floorName = floor.getName();

            Dormitory dormitory = floor.getDormitory();
            if (dormitory != null){
                dormitoryId = dormitory.getId();
                dormitoryName = dormitory.getName();
            }
        }
        User creator = duty.getCreator();
        if (creator != null){
            creatorId = creator.getId();
            creatorFirstName = creator.getFirstName();
            creatorLastName = creator.getLastName();
            creatorMiddleName = creator.getMiddleName();
        }
    }
}
