package javier.com.mydorm1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FloorRequestDto {
    private Long id;
    private String name;
    private List<Long> leaderIds;
    private Long dormId;
    private String randString;
}
