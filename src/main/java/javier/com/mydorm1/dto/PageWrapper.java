package javier.com.mydorm1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PageWrapper{
    private Long total;
    private Integer totalPages;
    private List<?> list;
}
