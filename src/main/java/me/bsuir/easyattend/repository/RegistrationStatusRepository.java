package me.bsuir.easyattend.repository;


import me.bsuir.easyattend.model.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationStatusRepository extends JpaRepository<RegistrationStatus, Long> {
}