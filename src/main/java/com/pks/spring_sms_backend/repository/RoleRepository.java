package com.pks.spring_sms_backend.repository;

import com.pks.spring_sms_backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByRoleName(String s);
}
