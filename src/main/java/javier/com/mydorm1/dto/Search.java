package javier.com.mydorm1.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Search {
    private String value;
    private Long dormId;
    private Long floorId;
    private Long roomId;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date date;
}
