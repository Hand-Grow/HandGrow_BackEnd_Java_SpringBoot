package com.handgrow.demo.repository;

import com.handgrow.demo.entity.Account;
import com.handgrow.demo.entity.Enterprise;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {
    Optional<Enterprise> findByAccount(Account account);
}
