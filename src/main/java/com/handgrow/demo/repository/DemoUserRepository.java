package com.handgrow.demo.repository;

import com.handgrow.demo.entity.DemoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DemoUserRepository extends JpaRepository<DemoUser, Long> {
    // You can custom query methods here if needed
    // In this case, basic CRUD operations are provided by JpaRepository so you may leave it empty
    Optional<DemoUser> findByUsername(String username);
}