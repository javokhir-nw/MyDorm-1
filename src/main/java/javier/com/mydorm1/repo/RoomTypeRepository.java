package javier.com.mydorm1.repo;

import javier.com.mydorm1.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    @Modifying
    @Query("""
        update RoomType
        set status = 'DELETED'
        where id = ?1
        """)
    void changeStatusToDeleted(Long id);
}
