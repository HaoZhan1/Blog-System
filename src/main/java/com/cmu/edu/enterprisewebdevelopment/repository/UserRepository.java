package com.cmu.edu.enterprisewebdevelopment.repository;

import com.cmu.edu.enterprisewebdevelopment.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String userName);
}
