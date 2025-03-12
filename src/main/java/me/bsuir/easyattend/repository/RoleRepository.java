package me.bsuir.easyattend.repository;

import me.bsuir.easyattend.model.Role;
import me.bsuir.easyattend.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name); // Change parameter type to RoleType
}