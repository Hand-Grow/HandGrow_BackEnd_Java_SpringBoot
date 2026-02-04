package com.handgrow.demo.repository;

import com.handgrow.demo.entity.Account;
import com.handgrow.demo.entity.Cooperative;
import com.handgrow.demo.entity.enums.Produce;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CooperativeRepository extends JpaRepository<Cooperative, UUID> {
    Optional<Cooperative> findByAccount(Account account);

    List<Cooperative> findByCommune(String commune);

    List<Cooperative> findByProvince(String province);

    List<Cooperative> findByProduce(Produce produce);

    List<Cooperative> findByCommuneAndProvince(String commune, String province);

    List<Cooperative> findByCommuneAndProduce(String commune, Produce produce);

    List<Cooperative> findByProvinceAndProduce(String province, Produce produce);

    List<Cooperative> findByCommuneAndProvinceAndProduce(String commune, String province, Produce produce);

    @Query("SELECT c FROM Cooperative c LEFT JOIN c.members m GROUP BY c ORDER BY COUNT(m) DESC")
    List<Cooperative> findAllOrderByMemberCount();
}
