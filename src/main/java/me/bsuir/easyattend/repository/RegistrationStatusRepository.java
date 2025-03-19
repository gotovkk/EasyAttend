package me.bsuir.easyattend.repository;


import me.bsuir.easyattend.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface RegistrationStatusRepository extends JpaRepository<RegistrationStatus, Long> {
    List<RegistrationStatus> findByUser_Id(Long userId); // Важно: "User_Id" соответствует полю в классе RegistrationStatus
    List<RegistrationStatus> findByEvent_Id(Long eventId); // Добавляем для поиска по event_id
    @Query("SELECT rs.user FROM RegistrationStatus rs WHERE rs.event.id = :eventId")
    List<User> findUsersByEvent_Id(@Param("eventId") Long eventId);}