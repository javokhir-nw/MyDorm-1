package javier.com.mydorm1.repo;

import javier.com.mydorm1.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Modifying
    @Query( """
        update Room
        set status = 'DELETED'
        where id = ?1
        """)
    void changeStatusToDeleteById(Long id);

    @Query("""
        select r from Room r where r.floor.id = ?1 and r.status = 'ACTIVE' order by r.number
        """)
    List<Room> findByFloorId(Long id);

    @Query("""
        select r from Room r where r.floor.id = ?1 and r.isRoom = true and r.status = 'ACTIVE' order by r.number
        """)
    List<Room> findAllBedroomByFloorId(Long id);

    @Query("select r from Room r where r.status = 'ACTIVE' and r.isRoom = false and r.floor.id = ?1")
    List<Room> findDutyRoomsByFloorId(Long floorId);
}
