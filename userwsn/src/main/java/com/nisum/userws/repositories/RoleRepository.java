package com.nisum.userws.repositories;

import com.nisum.userws.models.ERole;
import com.nisum.userws.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
