package com.cmu.edu.enterprisewebdevelopment.repository;

import com.cmu.edu.enterprisewebdevelopment.domain.Blog;
import com.cmu.edu.enterprisewebdevelopment.domain.User;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface BlogRepository extends JpaRepository<Blog, Long> {
    Blog findBlogById(long blogId);
    List<Blog> findAllByUser(User user);
    List<Blog> findAllByContentLike(String content);
}
