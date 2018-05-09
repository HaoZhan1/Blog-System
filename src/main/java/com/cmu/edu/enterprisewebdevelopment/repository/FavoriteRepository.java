package com.cmu.edu.enterprisewebdevelopment.repository;

import com.cmu.edu.enterprisewebdevelopment.domain.Favorite;
import com.cmu.edu.enterprisewebdevelopment.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Favorite findByUser(User user);
}
