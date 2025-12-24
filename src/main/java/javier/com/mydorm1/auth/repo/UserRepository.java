package javier.com.mydorm1.auth.repo;

import javier.com.mydorm1.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where lower(u.username) = lower(?1) ")
    User findByUsername(String username);

    @Query("select count(u.id) > 0 from User u where (?1 is null or u.id != ?1) and lower(u.username) = lower(?2) ")
    boolean existByUsername(Long id,String username);

    @Query("select u from User u join fetch u.roles where lower(u.username) = lower(?1)")
    User findByUsernameEager(String username);
}
