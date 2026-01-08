package javier.com.mydorm1.repo;

import javier.com.mydorm1.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query("select count(a.id) > 0 from Attendance a where a.floor.id = ?1 and date(a.createdDate) = date(?2)")
    boolean hasCreatedTodayAttendance(Long floorId, Date date);
}
