package com.github.truongbb.geneticschoolbusroutingalgorithm.service;

import com.github.truongbb.geneticschoolbusroutingalgorithm.config.SchoolBusConfiguration;
import com.github.truongbb.geneticschoolbusroutingalgorithm.dto.BusSchoolEntity;
import com.github.truongbb.geneticschoolbusroutingalgorithm.dto.Route;
import com.github.truongbb.geneticschoolbusroutingalgorithm.entity.BusStop;
import com.github.truongbb.geneticschoolbusroutingalgorithm.entity.DistanceMatrix;
import com.github.truongbb.geneticschoolbusroutingalgorithm.repository.BusStopRepository;
import com.github.truongbb.geneticschoolbusroutingalgorithm.repository.DistanceMatrixRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class SchoolBusServiceImpl implements SchoolBusService {

    SchoolBusConfiguration schoolBusConfiguration;
    BusStopRepository busStopRepository;
    DistanceMatrixRepository distanceMatrixRepository;

    private List<BusStop> busStops;
    private List<DistanceMatrix> distanceMatrices;
    private List<Route> routes;
    private List<BusSchoolEntity> population;
    private List<Integer> buses;

    public SchoolBusServiceImpl(BusStopRepository busStopRepository, DistanceMatrixRepository distanceMatrixRepository,
                                SchoolBusConfiguration schoolBusConfiguration) {
        this.busStopRepository = busStopRepository;
        this.distanceMatrixRepository = distanceMatrixRepository;
        this.schoolBusConfiguration = schoolBusConfiguration;
    }

    @Override
    public void generateSchoolBusRoute() {
        /**
         * 0. Init data
         * 1. Generate initial population
         * 2. Loop: selection, cross over, mutation
         *
         */

        this.initData();
    }

    private void initData() {
        busStops = busStopRepository.findAll();
        distanceMatrices = distanceMatrixRepository.findAll();
        buses = new ArrayList<>();
        for (int i = 0; i < schoolBusConfiguration.getBusNumber(); i++) {
            buses.add(i);
        }
    }

    private void generateInitialPopulation() {
        this.population = new ArrayList<>();

        while (this.population.size() < this.schoolBusConfiguration.getGenerationNumber()) {
            BusSchoolEntity entity = new BusSchoolEntity(this.schoolBusConfiguration.getBusNumber(), this.busStops.size());
            int index = new Random(this.schoolBusConfiguration.getBusNumber() - 1).nextInt();
            int bus = this.buses.get(index);
            this.buses.remove(index);

            while (this.busStops.size() > 0) {
                int i = new Random(this.busStops.size() - 1).nextInt();
                BusStop busStop = this.busStops.get(i);
                this.busStops.remove(i);

                if (!entity.assignBusToBusStop(bus, busStop, this.distanceMatrices,
                        this.schoolBusConfiguration.getVehicleCapacity(), this.schoolBusConfiguration.getMaxRiddingTime())) {
                    int j = new Random(this.buses.size() - 1).nextInt();
                    Integer busJ = this.buses.get(j);
                    this.buses.remove(j);
                    entity.assignBusToBusStop(busJ, busStop, this.distanceMatrices,
                            this.schoolBusConfiguration.getVehicleCapacity(), this.schoolBusConfiguration.getMaxRiddingTime());
                }
            }
            this.population.add(entity);
        }
    }

}
