package com.github.truongbb.geneticschoolbusroutingalgorithm;

import com.github.truongbb.geneticschoolbusroutingalgorithm.service.SchoolBusService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@AllArgsConstructor
@SpringBootApplication
public class GeneticSchoolBusRoutingAlgorithmApplication implements CommandLineRunner {

    SchoolBusService schoolBusService;

    public static void main(String[] args) {
        SpringApplication.run(GeneticSchoolBusRoutingAlgorithmApplication.class, args);
    }

    @Override
    public void run(String... args) {
        schoolBusService.generateSchoolBusRoute();
    }

}
