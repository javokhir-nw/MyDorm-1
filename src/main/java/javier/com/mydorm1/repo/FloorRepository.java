package javier.com.mydorm1.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {

    @Query(
            """
        select f from Floor f where f.dormitory.id = ?1
                and (?2 is null or f.name ilike %?2% or f.dormitory.name ilike %?2%)
        """
    )
    Page<Floor> findAllByDormId(Long dormId, String value, Pageable pageable);

    @Query(
            """
        select f from Floor f where f.dormitory.id = ?1
        """
    )
    List<Floor> findAllByDormId(Long dormId);
}
