package com.github.truongbb.geneticschoolbusroutingalgorithm.service;

import com.github.truongbb.geneticschoolbusroutingalgorithm.repository.BusStopRepository;
import com.github.truongbb.geneticschoolbusroutingalgorithm.repository.DistanceMatrixRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SchoolBusServiceImpl implements SchoolBusService {

    BusStopRepository busStopRepository;
    DistanceMatrixRepository distanceMatrixRepository;

    @Override
    public void generateSchoolBusRoute() {
        /**
         * 0. Init data
         * 1. Generate initial population
         * 2. Loop: selection, cross over, mutation
         *
         */
    }


}
