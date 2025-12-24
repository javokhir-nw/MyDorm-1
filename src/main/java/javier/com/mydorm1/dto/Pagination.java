package javier.com.mydorm1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Pagination<T> {
    private int page;
    private int size;
    private T search;
}
