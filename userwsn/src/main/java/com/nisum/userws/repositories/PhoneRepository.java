package com.nisum.userws.repositories;

import com.nisum.userws.models.ERole;
import com.nisum.userws.models.Phone;
import com.nisum.userws.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhoneRepository  extends JpaRepository<Phone, Long> {

}
