package javier.com.mydorm1.repo;

import javier.com.mydorm1.model.Room;
import javier.com.mydorm1.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    @Modifying
    @Query("""
        update RoomType
        set status = 'DELETED'
        where id = ?1
        """)
    void changeStatusToDeleted(Long id);

    @Query("select rt from RoomType rt where lower(rt.code) = lower(?1) ")
    RoomType findByCode(String bedroom);

    @Query("select rt from RoomType rt where rt.status = 'ACTIVE' order by rt.id desc")
    List<RoomType> findAllStatusActive();
}
