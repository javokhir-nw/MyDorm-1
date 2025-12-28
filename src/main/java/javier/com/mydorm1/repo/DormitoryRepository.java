package javier.com.mydorm1.repo;

import javier.com.mydorm1.dto.Search;
import javier.com.mydorm1.model.Dormitory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DormitoryRepository extends JpaRepository<Dormitory,Long> {

    @Query(
        """
        select d from Dormitory d where d.status = 'ACTIVE' and (?1 is null or d.name ilike %?1%)
        """
    )
    Page<Dormitory> findAll(String value, Pageable pageable);

    @Modifying
    @Query("""
        update Dormitory
        set status = 'DELETED'
        where id = ?1
        """)
    void changeStatusToDeleted(Long id);
}
