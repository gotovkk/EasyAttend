package me.bsuir.easyattend.repository;

import jakarta.transaction.Transactional;
import java.util.List;
import me.bsuir.easyattend.model.RegistrationStatus;
import me.bsuir.easyattend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationStatusRepository extends JpaRepository<RegistrationStatus, Long> {
    List<RegistrationStatus> findByUserId(Long userId);

    List<RegistrationStatus> findByEventId(Long eventId);

    @Query("SELECT rs.user FROM RegistrationStatus rs WHERE rs.event.id = :eventId")
    List<User> findUsersByEventId(@Param("eventId") Long eventId);

    @Transactional
    void deleteByEventIdAndUserId(Long eventId, Long userId);
}