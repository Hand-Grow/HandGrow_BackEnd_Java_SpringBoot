package com.handgrow.demo.repository;

import com.handgrow.demo.entity.Account;
import com.handgrow.demo.entity.Enterprise;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnterpriseRepository extends JpaRepository<Enterprise, UUID> {
    Optional<Enterprise> findByAccount(Account account);

    Optional<Enterprise> findByAccountId(UUID accountId);
}
