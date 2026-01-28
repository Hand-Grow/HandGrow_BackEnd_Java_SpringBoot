package com.handgrow.demo.repository;

import com.handgrow.demo.entity.Account;
import com.handgrow.demo.entity.Cooperative;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CooperativeRepository extends JpaRepository<Cooperative, Long> {
    Optional<Cooperative> findByAccount(Account account);
}
