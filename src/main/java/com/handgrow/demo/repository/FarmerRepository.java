package com.handgrow.demo.repository;

import com.handgrow.demo.entity.Account;
import com.handgrow.demo.entity.Farmer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, Long> {
    Optional<Farmer> findByAccount(Account account);
}
