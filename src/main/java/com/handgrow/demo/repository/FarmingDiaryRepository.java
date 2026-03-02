package com.handgrow.demo.repository;

import com.handgrow.demo.entity.FarmingDiary;
import com.handgrow.demo.entity.enums.ActivityType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FarmingDiaryRepository extends JpaRepository<FarmingDiary, UUID> {

    List<FarmingDiary> findByPlotIdAndActivityDateBetween(UUID plotId, LocalDate startDate, LocalDate endDate);

    List<FarmingDiary> findByFarmerIdAndActivityDateBetween(UUID farmerId, LocalDate startDate, LocalDate endDate);

    List<FarmingDiary> findByPlotIdAndActivityType(UUID plotId, ActivityType activityType);

    @Query("SELECT COALESCE(SUM(d.expense), 0) FROM FarmingDiary d "
            + "WHERE d.plot.id = :plotId AND d.activityDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalExpense(
            @Param("plotId") UUID plotId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
