package com.cmu.edu.enterprisewebdevelopment.repository;

import com.cmu.edu.enterprisewebdevelopment.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findRoleByName(String name);
}
