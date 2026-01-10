package javier.com.mydorm1.repo;

import javier.com.mydorm1.model.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query("select count(a.id) > 0 from Attendance a where a.floor.id = ?1 and date(a.createdDate) = date(?2)")
    boolean hasCreatedTodayAttendance(Long floorId, Date date);

    @Query("select coalesce(a.absentUserIds,'') from Attendance a where a.floor.id = ?1 and date(a.createdDate) = date(?2)")
    String getAbsentUsersString(Long floorId, Date date);

    @Query("""
        select a from Attendance a
        left join Floor f on f.id = a.floor.id
        where ( ?1 is null or
                f.name ilike %?1% or
                f.dormitory.name ilike %?1% )
        and (?2 is null or f.dormitory.id = ?2)
        and (?3 is null or f.id = ?3)
        and date(a.createdDate) = date(?4)
        """)
    Page<Attendance> list(String value, Long dormId, Long floorId, Date date, Pageable of);

    @Query("select a from Attendance a where a.floor.id = ?1 and date(a.createdDate) = date(?2)")
    Optional<Attendance> getTodayAttendance(Long floorId, Date date);
}
