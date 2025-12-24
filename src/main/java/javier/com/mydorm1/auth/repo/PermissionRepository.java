package javier.com.mydorm1.auth.repo;

import javier.com.mydorm1.auth.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface PermissionRepository extends JpaRepository<Permission,Long> {

    @Query("select p from Permission p where lower(p.name) = lower(?1) ")
    Permission getPermissionByName(String code);

    @Query("select count(p.id) > 0 from Permission p where lower(p.name) = lower(?1) ")
    boolean existByName(String name);
}
