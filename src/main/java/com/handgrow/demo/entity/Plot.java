package com.handgrow.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "plots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plot extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "location")
    private String location;

    @Column(name = "area")
    private Double area;

    @Column(name = "area_unit")
    private String areaUnit;
}
