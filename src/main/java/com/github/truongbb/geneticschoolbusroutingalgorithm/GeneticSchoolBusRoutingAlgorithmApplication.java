package com.github.truongbb.geneticschoolbusroutingalgorithm;

import com.github.truongbb.geneticschoolbusroutingalgorithm.config.SchoolBusConfiguration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GeneticSchoolBusRoutingAlgorithmApplication implements CommandLineRunner {
    SchoolBusConfiguration schoolBusConfiguration;
    public static void main(String[] args) {
        SpringApplication.run(GeneticSchoolBusRoutingAlgorithmApplication.class, args);
    }


    @Override
    public void run(String... args) {

    }

}
