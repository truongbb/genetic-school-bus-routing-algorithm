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

    private int[] students;

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

    private void generateBase() {
        int[] student = this.busStops.stream().mapToInt(BusStop::getNumberOfStudent).toArray();
        this.students = student;
        this.generateInitialPopulation();
        this.sort();
        int generation = 0;
        System.out.println("Generation " + generation + " - Best solution fitness: ");
        for (generation = 1; generation < this.schoolBusConfiguration.getGenerationNumber(); generation++) {
            this.population = new ArrayList<>();
            while (this.population.size() < this.schoolBusConfiguration.getPopulationSize()) {
                BusSchoolEntity child = null;
                double r = new Random().nextDouble();
                if (r < this.schoolBusConfiguration.getCrossOverRate()) {

                    // Select 2 parents by seed selection
                    BusSchoolEntity male = null;
                    BusSchoolEntity female = null;
                    this.selectTwoParents(male, female);

                    // Apply crossover and select the better child
                    child = this.Crossoverrate(male, female);

                    // Apply 6-case mutation to select child
                    int index = 6;
                    child = this.mutationCase(child, index);

                } else {
                    r = new Random().nextDouble();
                    // Select an individual randomly from the curent population
                    BusSchoolEntity entity = null;
                    this.selectOneParent(entity);

                    if (r < this.schoolBusConfiguration.getMutationRate()) {
                        // Apply 5-case mutation to the selected individual
                        int index = 5;
                        child = this.mutationCase(entity, index);
                    } else {
                        // Copy the selected individual
                        child = new BusSchoolEntity(this.schoolBusConfiguration.getBusNumber(), entity.getChromosome());
                    }
                }

                // repair the new individual
                this.repair(child);

                // add the new individual to the new population
                population.add(child);
            }
            System.out.println("Generation " + generation + " - Best solution fitness: ");
        }
        System.out.println("Solution found");
    }

    private void sort() {
        for (int i = 0; i < this.population.size(); i++) {

        }
    }

    private BusSchoolEntity Crossoverrate(BusSchoolEntity male, BusSchoolEntity female) {
        int point1, point2;
        point1 = new Random(this.busStops.size() - 2).nextInt();
        do {
            point2 = new Random(this.busStops.size() - 2).nextInt();
        } while (point1 == point2);
        if (point1 > point2) {
            // swap point1 and point2
            int temp = point1;
            point1 = point2;
            point2 = temp;
        }

        int[][] maleChromosome = male.getChromosome();
        int[][] femaleChromosome = female.getChromosome();

        int[][] child1Chromsome = new int[this.busStops.size()][];
        int[][] child2Chromsome = new int[this.busStops.size()][];

        for (int i = 0; i <= point1; i++) {
            child1Chromsome[i] = new int[]{maleChromosome[i][0], maleChromosome[i][1]};
            child2Chromsome[i] = new int[]{femaleChromosome[i][0], femaleChromosome[i][1]};
        }
        for (int i = point1 + 1; i <= point2; i++) {
            child1Chromsome[i] = new int[]{femaleChromosome[i][0], femaleChromosome[i][1]};
            child2Chromsome[i] = new int[]{maleChromosome[i][0], maleChromosome[i][1]};
        }
        for (int i = point2 + 1; i <= this.busStops.size() - 1; i++) {
            child1Chromsome[i] = new int[]{maleChromosome[i][0], maleChromosome[i][1]};
            child2Chromsome[i] = new int[]{femaleChromosome[i][0], femaleChromosome[i][1]};
        }
        BusSchoolEntity child1 = new BusSchoolEntity(this.schoolBusConfiguration.getBusNumber(), child1Chromsome);
        BusSchoolEntity child2 = new BusSchoolEntity(this.schoolBusConfiguration.getBusNumber(), child2Chromsome);

        double fitness1 = this.calculateFitness(child1);
        double fitness2 = this.calculateFitness(child2);

        return fitness1 > fitness2 ? child2 : child1;
    }

    private double calculateFitness(BusSchoolEntity child) {
        List<Double> routeLengths = child.getRouteLengths(this.distanceMatrices);
        double dist = 0;
        for (double route : routeLengths) {
            dist += route;
            if (route > this.schoolBusConfiguration.getMaxRiddingTime()) {
                dist += 10 * (route - this.schoolBusConfiguration.getMaxRiddingTime());
            }
        }
        return dist;
    }

    private void repair(BusSchoolEntity child) {
        child.encode();
    }

    private BusSchoolEntity mutationCase(BusSchoolEntity entity, int index) {
        int[][] chromosome0 = entity.getChromosome();
        int[][][] mutations = new int[5][][];

        // copy to 5 mutations
        for (int i = 0; i < 5; i++) {
            mutations[i] = new int[chromosome0.length][];
            for (int j = 0; j < chromosome0.length; j++) {
                mutations[i][j] = new int[]{chromosome0[j][0], chromosome0[j][1]};
            }
        }

        // apply 3-points permutation;
        int point1, point2, point3;
        point1 = new Random(chromosome0.length - 1).nextInt();
        do {
            point2 = new Random(chromosome0.length - 1).nextInt();
        } while (point2 == point1);

        do {
            point3 = new Random(chromosome0.length - 1).nextInt();
        } while (point3 == point1 || point3 == point2);

        mutations[0] = this._setGenePosition(chromosome0, mutations[0], point1, point2, point3, point1, point3, point2);
        mutations[1] = this._setGenePosition(chromosome0, mutations[1], point1, point2, point3, point2, point1, point3);
        mutations[2] = this._setGenePosition(chromosome0, mutations[2], point1, point2, point3, point2, point3, point1);
        mutations[3] = this._setGenePosition(chromosome0, mutations[3], point1, point2, point3, point3, point1, point2);
        mutations[4] = this._setGenePosition(chromosome0, mutations[4], point1, point2, point3, point3, point2, point1);

        BusSchoolEntity[] candidates = null;
        int minIndex = 0;
        //create 5 entities and choose the best one
        if (index == 5) {
            candidates = new BusSchoolEntity[index];
            double minFitness = Double.MAX_VALUE;
            double currentFitness;

            for (int i = 0; i < 5; i++) {
                candidates[i] = new BusSchoolEntity(this.schoolBusConfiguration.getBusNumber(), mutations[i]);
                currentFitness = this.calculateFitness(candidates[i]);
                if (currentFitness < minFitness) {
                    minFitness = currentFitness;
                    minIndex = i;
                }
            }
        }
        // create 6 entities (5 new ones) and choose the best one
        else if (index == 6) {
            candidates = new BusSchoolEntity[index];
            double minFitness = Double.MAX_VALUE;
            double currentFitness;

            for (int i = 0; i < 5; i++) {
                candidates[i + 1] = new BusSchoolEntity(this.schoolBusConfiguration.getBusNumber(), mutations[i]);
                currentFitness = this.calculateFitness(candidates[i + 1]);
                if (currentFitness < minFitness) {
                    minFitness = currentFitness;
                    minIndex = i + 1;
                }
            }
        }
        return candidates[minIndex];
    }

    private int[][] _setGenePosition(int[][] chromosome0, int[][] chromosome, int point1, int point2, int point3, int point11, int point31, int point21) {
        chromosome[point11][0] = chromosome0[point1][0];
        chromosome[point11][1] = chromosome0[point1][1];

        chromosome[point21][0] = chromosome0[point2][0];
        chromosome[point21][1] = chromosome0[point2][1];

        chromosome[point31][0] = chromosome0[point3][0];
        chromosome[point31][1] = chromosome0[point3][1];

        return chromosome;
    }

    // Select one entity randomly from the current population
    private void selectOneParent(BusSchoolEntity entity) {
        int index = new Random(this.schoolBusConfiguration.getPopulationSize() - 1).nextInt();
        entity = this.population.get(index);
    }

    // Select male and female parents from
    private void selectTwoParents(BusSchoolEntity male, BusSchoolEntity female) {
        int maleIndex, femaleIndex;
        maleIndex = new Random((int) Math.round(this.schoolBusConfiguration.getSelectionRate() * this.schoolBusConfiguration.getPopulationSize())).nextInt();
        do {
            femaleIndex = new Random(this.population.size() - 1).nextInt();
        } while (femaleIndex == maleIndex);
        male = this.population.get(maleIndex);
        female = this.population.get(femaleIndex);
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
