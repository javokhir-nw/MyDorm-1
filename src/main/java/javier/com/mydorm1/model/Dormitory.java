package javier.com.mydorm1.model;

import jakarta.persistence.*;
import javier.com.mydorm1.auth.model.Status;
import javier.com.mydorm1.auth.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "dorms")
@Entity
public class Dormitory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner; // -> mudira

    @Enumerated(EnumType.STRING)
    private Status status;
}
