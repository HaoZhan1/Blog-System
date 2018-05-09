package com.cmu.edu.enterprisewebdevelopment.repository;

import com.cmu.edu.enterprisewebdevelopment.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
