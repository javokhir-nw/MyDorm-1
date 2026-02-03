package javier.com.mydorm1.repo;

import javier.com.mydorm1.dto.DutyDto;
import javier.com.mydorm1.model.Duty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DutyRepository extends JpaRepository<Duty,Long> {

    @Query("select d from Duty d where d.floor.id = ?1 and date(d.createdDate) = date(?2)")
    Duty getTodayDuty(Long floorId, Date date);

    @Query("""
        select new javier.com.mydorm1.dto.DutyDto(d) from Duty d
        left join d.floor f
        left join f.dormitory dorm
        left join d.creator c
        where (?1 is null or f.id = ?1)
        and (?2 is null or dorm.id = ?2)
        and (date(?3) is null or date(d.createdDate) = date(?3))
        """)
    Page<DutyDto> findAllByPagination(Long floorId, Long dormId, Date date, Pageable of);
}
