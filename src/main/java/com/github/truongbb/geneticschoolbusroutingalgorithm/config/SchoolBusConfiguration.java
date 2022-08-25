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

    @Value("${application.elitis-number}")
    Integer elitisNumber;

    @Value("${application.population-size}")
    Long populationSize;

    @Value("${application.generation-number}")
    Integer generationNumber;

    @Value("${application.bus-number}")
    Integer busNumber;

    @Value("${application.vehicle-capacity}")
    Integer vehicleCapacity;

    @Value("${application.max-ridding-time}")
    Integer maxRiddingTime;

}
