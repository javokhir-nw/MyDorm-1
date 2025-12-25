package javier.com.mydorm1.auth.repo;

import javier.com.mydorm1.auth.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("select r  from Role r where lower(r.code) = lower(?1) ")
    Role findByCode(String code);

    @Query("select r  from Role r left join fetch r.permissions where lower(r.code) = lower(?1) ")
    Role findByCodeEager(String code);
}
