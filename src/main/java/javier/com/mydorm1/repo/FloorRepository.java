package javier.com.mydorm1.repo;

import javier.com.mydorm1.auth.model.Status;
import javier.com.mydorm1.model.Floor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {

    @Query(
            """
        select f from Floor f where f.dormitory.id = ?1 and f.status = 'ACTIVE'
                and (?2 is null or f.name ilike %?2% or f.dormitory.name ilike %?2%) order by f.id
        """
    )
    Page<Floor> findAllByDormId(Long dormId, String value, Pageable pageable);

    @Query(
            """
        select f from Floor f where f.dormitory.id = ?1 order by f.id
        """
    )
    List<Floor> findAllByDormId(Long dormId);

    @Modifying
    @Query("""
        update Floor
        set status = ?2
        where id = ?1
        """)
    void updateFloorStatus(Long id, Status status);

    @Query("select f from Floor f where lower(f.floorTelegramIdentity) = lower(?1) ")
    Floor findByToken(String token);
}
