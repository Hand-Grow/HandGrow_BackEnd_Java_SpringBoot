package com.handgrow.demo.repository;

import com.handgrow.demo.entity.Plot;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlotRepository extends JpaRepository<Plot, UUID> {
    List<Plot> findByFarmerId(UUID farmerId);

    Optional<Plot> findByFarmerIdAndName(UUID farmerId, String name);
}
