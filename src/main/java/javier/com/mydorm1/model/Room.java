package javier.com.mydorm1.model;

import jakarta.persistence.*;
import javier.com.mydorm1.auth.model.Status;
import javier.com.mydorm1.repo.Floor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static java.lang.Boolean.TRUE;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String number;

    private Integer capacity = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    private Boolean isRoom = TRUE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id")
    private Floor floor;

    @Enumerated(EnumType.STRING)
    private Status status;
}
