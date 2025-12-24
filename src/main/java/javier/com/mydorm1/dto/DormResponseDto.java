package javier.com.mydorm1.dto;

import jakarta.persistence.*;
import javier.com.mydorm1.auth.model.User;
import javier.com.mydorm1.model.Dormitory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DormResponseDto {
    private Long id;
    private String name;
    private Long ownerId;
    private String ownerFirstName;
    private String ownerLastName;
    private String ownerMiddleName;
    private List<FloorResponseDto> floors;

    public DormResponseDto(Dormitory dorm) {
        id = dorm.getId();
        name = dorm.getName();
        User owner = dorm.getOwner();
        if (owner != null) {
            ownerId = owner.getId();
            ownerFirstName = owner.getFirstName();
            ownerLastName = owner.getLastName();
            ownerMiddleName = owner.getMiddleName();
        }
    }
}
