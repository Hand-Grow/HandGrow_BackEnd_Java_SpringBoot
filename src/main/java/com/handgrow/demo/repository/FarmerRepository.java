package com.handgrow.demo.repository;

import com.handgrow.demo.entity.Account;
import com.handgrow.demo.entity.Farmer;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, UUID> {
    Optional<Farmer> findByAccount(Account account);
    Optional<Farmer> findByAccountId(UUID accountId);

    @Query("SELECT f FROM Farmer f WHERE f.account.username = :username")
    Optional<Farmer> findByUsername(@Param("username") String username);
}
