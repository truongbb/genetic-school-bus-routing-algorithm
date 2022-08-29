package com.github.truongbb.geneticschoolbusroutingalgorithm.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class SchoolBusConfiguration {

    @Value("${application.genetic-algorithm-rate.cross-over-rate}")
    Double crossOverRate;

    @Value("${application.genetic-algorithm-rate.mutation-rate}")
    Double mutationRate;

    @Value("${application.genetic-algorithm-rate.selection-rate}")
    Double selectionRate;

    @Value("${application.elites-number}")
    Integer elitesNumber;

    @Value("${application.population-size}")
    Integer populationSize;

    @Value("${application.generation-number}")
    Integer generationNumber;

    @Value("${application.bus-number}")
    Integer busNumber;

    @Value("${application.vehicle-capacity}")
    Integer vehicleCapacity;

    @Value("${application.max-ridding-time}")
    Integer maxRiddingTime;

    @Value("${application.school-stop-id}")
    Integer schoolStopId;

}
