package javier.com.mydorm1.repo;

import javier.com.mydorm1.model.DutyItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface DutyItemRepository extends JpaRepository<DutyItem, Long> {

    @Query("select di from DutyItem di where di.room.floor.id = ?1 and di.room.id = ?2 and date(di.createdDate) = date(?3)")
    DutyItem getTodayDutyItemByRoomId(Long floorId, Long roomId, Date date);

    @Query(
            """
            select di from DutyItem di
            where di.duty.floor.id = ?1 and date(di.duty.createdDate) = date(?2)
            """
    )
    List<DutyItem> getTodayDutyItemsByFloorId(Long floorId, Date date);
}
