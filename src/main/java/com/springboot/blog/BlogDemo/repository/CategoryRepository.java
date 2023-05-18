package com.springboot.blog.BlogDemo.repository;

import com.springboot.blog.BlogDemo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {
}
