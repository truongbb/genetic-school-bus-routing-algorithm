package com.github.truongbb.geneticschoolbusroutingalgorithm.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "DISTANCE_MATRIX")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DistanceMatrix implements Serializable {

    @Id
    @Column(nullable = false)
    Integer id;

    @ManyToOne(targetEntity = BusStop.class)
    @JoinColumn(name = "start_bus_stop_id")
    BusStop startBusStop;

    @ManyToOne(targetEntity = BusStop.class)
    @JoinColumn(name = "end_bus_stop_id")
    BusStop endBusStop;

    @Column(nullable = false)
    Double distance;

    @Column(nullable = false)
    Double durations;

}
