package javier.com.mydorm1.model;

import jakarta.persistence.*;
import javier.com.mydorm1.auth.model.Status;
import javier.com.mydorm1.auth.model.User;
import javier.com.mydorm1.repo.Floor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "attandences")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String absentUserIds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id")
    private Floor floor;

    @CreatedDate
    private Date createdDate;
}
