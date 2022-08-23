package com.github.truongbb.geneticschoolbusroutingalgorithm.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "DISTANCE_MATRIX")
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
