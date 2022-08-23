package com.github.truongbb.geneticschoolbusroutingalgorithm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

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

}
