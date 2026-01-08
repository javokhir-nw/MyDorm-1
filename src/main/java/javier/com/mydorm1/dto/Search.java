package javier.com.mydorm1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Search {
    private String value;
    private Long dormId;
    private Long floorId;
    private Long roomId;
}
