package com.pks.spring_sms_backend.repository;

import com.pks.spring_sms_backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {
}
