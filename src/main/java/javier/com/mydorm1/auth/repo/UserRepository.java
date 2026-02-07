package javier.com.mydorm1.auth.repo;

import javier.com.mydorm1.auth.dto.UserDto;
import javier.com.mydorm1.auth.model.Status;
import javier.com.mydorm1.auth.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where lower(u.username) = lower(?1) ")
    User findByUsername(String username);

    @Query("select count(u.id) > 0 from User u where (?1 is null or u.id != ?1) and lower(u.username) = lower(?2) ")
    boolean existByUsername(Long id,String username);

    @Query("select u from User u join fetch u.roles where lower(u.username) = lower(?1)")
    User findByUsernameEager(String username);

    @Query("""
        select u from User u
        where (
                ?1 is null or
                u.firstName ilike %?1% or
                u.lastName ilike %?1% or
                u.middleName ilike %?1% or
                concat(u.lastName,' ',u.firstName,' ',u.middleName) ilike %?1%
        ) and
        (?2 is null or u.dormitory.id = ?2) and
        (?3 is null or u.floor.id = ?3) and
        u.status = 'ACTIVE' and  (u.username is null or u.username != 'admin') and
        (?4 is null or u.room.id = ?4)
        order by u.lastName,u.firstName,u.middleName,u.id desc
        """)
    Page<User> findAllByPagination(String value, Long dormId, Long floorId, Long roomId, Pageable of);

    @Modifying
    @Query("""
        update User
        set status = ?2
        where id = ?1
        """)
    void changeUserStatus(Long id, Status status);

    @Query("""
        select u from User u left join fetch u.floor left join fetch u.room where u.status = 'ACTIVE' and (?1 is null or lower(u.telegramUsername) = lower(?1)) and (?2 is null or u.telegramId = ?2)
        """)
    User findByTelegramUsernameOrTelegramId(String userName, Long telegramId);

    @Query("select u from User u left join fetch u.room where u.floor.id = ?1 order by u.room.number asc")
    List<User> findAllUsersFetchRoomByFloorId(Long id);

    @Query("select count(u) > 0 from User u where (u.telegramId = ?1 or u.telegramUsername = ?2)")
    Boolean existByTelegramUsernameAndTelegramId(Long userId, String userName);

    @Query("select u from User u left join fetch u.room where u.floor.id = ?1 order by u.room.number ")
    List<User> findAllPresentUsers(Long floorId);

    @Query(value = "select now()",nativeQuery = true)
    Date selectNow();

    @Query(value = """
            select u.* from users u
            left join user_roles ur on ur.user_id = u.id
            left join roles r on r.id = ur.role_id
            where u.status = 'ACTIVE' and r.code = 'ROLE_CAPTAIN' and u.floor_id is not null
            """, nativeQuery = true)
    List<User> findAllCaptains();

    @Query("select new javier.com.mydorm1.auth.dto.UserDto(u) from User u where u.id in (?1)")
    List<UserDto> findAllByIds(Set<Long> dutyUserIds);
}
