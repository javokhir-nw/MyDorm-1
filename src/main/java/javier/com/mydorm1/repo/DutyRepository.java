package javier.com.mydorm1.repo;

import javier.com.mydorm1.auth.model.User;
import javier.com.mydorm1.model.Duty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface DutyRepository extends JpaRepository<Duty,Long> {

    @Query("""
            select count(d) > 0 from Duty d where d.floor.id = ?1 and date(d.createdDate) = date(?2)
            """)
    boolean existTodayDuty(Long floorId, Date date);

    @Query("select d from Duty d where d.floor.id = ?1 and date(d.createdDate) = date(?2)")
    Duty getTodayDuty(Long floorId, Date date);
}
