package javier.com.mydorm1.repo;

import jakarta.persistence.*;
import javier.com.mydorm1.auth.model.Status;
import javier.com.mydorm1.auth.model.User;
import javier.com.mydorm1.model.Dormitory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "floors")
@Entity
public class Floor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne
    @JoinColumn(name = "leader_id")
    private User leader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dormitory_id")
    private Dormitory dormitory;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;
}
