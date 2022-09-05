package com.github.truongbb.geneticschoolbusroutingalgorithm.service;

import com.github.truongbb.geneticschoolbusroutingalgorithm.config.SchoolBusConfiguration;
import com.github.truongbb.geneticschoolbusroutingalgorithm.dto.BusSchoolEntity;
import com.github.truongbb.geneticschoolbusroutingalgorithm.dto.RepresentativeEntity;
import com.github.truongbb.geneticschoolbusroutingalgorithm.dto.Route;
import com.github.truongbb.geneticschoolbusroutingalgorithm.entity.BusStop;
import com.github.truongbb.geneticschoolbusroutingalgorithm.entity.DistanceMatrix;
import com.github.truongbb.geneticschoolbusroutingalgorithm.repository.BusStopRepository;
import com.github.truongbb.geneticschoolbusroutingalgorithm.repository.DistanceMatrixRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
        this.generateInitialPopulation();
        this.sort();
        int generation = 0;
        System.out.println("Generation " + generation + " - Best solution fitness: " + this.population.get(0).getFitness() + " (" + this.getBestSolutionRoutesLength(this.distanceMatrices) + " minutes)");
        for (generation = 1; generation <= this.schoolBusConfiguration.getGenerationNumber(); generation++) {
            List<BusSchoolEntity> newPopulation = new ArrayList<>();
            while (newPopulation.size() < this.schoolBusConfiguration.getPopulationSize()) {
                BusSchoolEntity child = null;
                double r = new Random().nextDouble();
                if (r < this.schoolBusConfiguration.getCrossOverRate()) {

                    // Select 2 parents by seed selection
                    RepresentativeEntity representativeEntity = this.selectTwoParents();
                    BusSchoolEntity male = representativeEntity.getMale();
                    BusSchoolEntity female = representativeEntity.getFemale();

                    // Apply crossover and select the better child
                    child = this.crossOver(male, female);

                    // Apply 6-case mutation to select child
                    child = this.mutationCase(child, 6);

                } else {
                    r = new Random().nextDouble();
                    // Select an individual randomly from the curent population
                    BusSchoolEntity entity = this.selectOneParent();

                    if (r < this.schoolBusConfiguration.getMutationRate()) {
                        // Apply 5-case mutation to the selected individual
                        child = this.mutationCase(entity, 5);
                    } else {
                        // Copy the selected individual
                        child = new BusSchoolEntity(this.schoolBusConfiguration.getBusNumber(), entity.getChromosome());
                    }
                }

                // repair the new individual
                child = this.repair(child);

                // add the new individual to the new population
                newPopulation.add(child);
            }
            this.updateElites(newPopulation);
            this.population = newPopulation;
            this.sort();
            System.out.println("Generation " + generation + " - Best solution fitness: " + this.population.get(0).getFitness() + " (" + this.getBestSolutionRoutesLength(this.distanceMatrices) + " minutes)");
        }
        System.out.println("Solution found");
    }

    private String getBestSolutionRoutesLength(List<DistanceMatrix> distanceMatrices) {
        List<Double> lengths = this.population.get(0).getRouteLengths(distanceMatrices, this.getSchoolStop());
        return lengths.stream().map(String::valueOf).collect(Collectors.joining(", "));
    }

    private void updateElites(List<BusSchoolEntity> newPopulation) {
        for (BusSchoolEntity busSchoolEntity : newPopulation) {
            busSchoolEntity.setFitness(this.calculateFitness(busSchoolEntity));
        }
        newPopulation.sort(Comparator.comparingDouble(BusSchoolEntity::getFitness));
        int currentSize = newPopulation.size();
        for (int i = 0; i < this.schoolBusConfiguration.getElitesNumber(); i++) {
            newPopulation.set(currentSize - i - 1, this.population.get(i));
        }
    }

    private void initData() {
        busStops = busStopRepository.findAll();
        distanceMatrices = distanceMatrixRepository.findAll();
        buses = new ArrayList<>();
        for (int i = 1; i <= schoolBusConfiguration.getBusNumber(); i++) {
            buses.add(i);
        }
    }

    private void sort() {
        for (BusSchoolEntity busSchoolEntity : this.population) {
            busSchoolEntity.setFitness(this.calculateFitness(busSchoolEntity));
        }
        this.population.sort(Comparator.comparingDouble(BusSchoolEntity::getFitness));
    }

    private BusSchoolEntity crossOver(BusSchoolEntity male, BusSchoolEntity female) {
        int point1, point2;
        point1 = new Random().nextInt(this.busStops.size() - 2);
        do {
            point2 = new Random().nextInt(this.busStops.size() - 2);
        } while (point1 == point2);
        if (point1 > point2) {
            // swap point1 and point2
            int temp = point1;
            point1 = point2;
            point2 = temp;
        }

        int[][] maleChromosome = male.getChromosome();
        int[][] femaleChromosome = female.getChromosome();

        int[][] child1Chromosome = new int[this.busStops.size()][];
        int[][] child2Chromosome = new int[this.busStops.size()][];

        for (int i = 0; i <= point1; i++) {
            child1Chromosome[i] = new int[]{maleChromosome[i][0], maleChromosome[i][1]};
            child2Chromosome[i] = new int[]{femaleChromosome[i][0], femaleChromosome[i][1]};
        }
        for (int i = point1 + 1; i <= point2; i++) {
            child1Chromosome[i] = new int[]{femaleChromosome[i][0], femaleChromosome[i][1]};
            child2Chromosome[i] = new int[]{maleChromosome[i][0], maleChromosome[i][1]};
        }
        for (int i = point2 + 1; i <= this.busStops.size() - 1; i++) {
            child1Chromosome[i] = new int[]{maleChromosome[i][0], maleChromosome[i][1]};
            child2Chromosome[i] = new int[]{femaleChromosome[i][0], femaleChromosome[i][1]};
        }
        BusSchoolEntity child1 = new BusSchoolEntity(this.schoolBusConfiguration.getBusNumber(), child1Chromosome);
        BusSchoolEntity child2 = new BusSchoolEntity(this.schoolBusConfiguration.getBusNumber(), child2Chromosome);

        double fitness1 = this.calculateFitness(child1);
        double fitness2 = this.calculateFitness(child2);

        return fitness1 > fitness2 ? child2 : child1;
    }

    private double calculateFitness(BusSchoolEntity child) {
        List<Double> routeLengths = child.getRouteLengths(this.distanceMatrices, this.getSchoolStop());
        double dist = 0;
        for (double route : routeLengths) {
            dist += route;
            if (route > this.schoolBusConfiguration.getMaxRiddingTime()) {
                dist += 10 * (route - this.schoolBusConfiguration.getMaxRiddingTime());
            }
        }
        return dist;
    }

    private BusStop getSchoolStop() {
        return this.busStops
                .stream()
                .filter(bst -> bst.getId().equals(schoolBusConfiguration.getSchoolStopId()))
                .findFirst()
                .orElse(null);
    }

    private BusSchoolEntity repair(BusSchoolEntity child) {
        return child.fixBusCapacities(this.busStops, schoolBusConfiguration.getVehicleCapacity()).encode();
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
        point1 = new Random().nextInt(chromosome0.length - 1);
        do {
            point2 = new Random().nextInt(chromosome0.length - 1);
        } while (point2 == point1);

        do {
            point3 = new Random().nextInt(chromosome0.length - 1);
        } while (point3 == point1 || point3 == point2);

        this._setGenePosition(chromosome0, mutations[0], point1, point2, point3, point1, point3, point2);
        this._setGenePosition(chromosome0, mutations[1], point1, point2, point3, point2, point1, point3);
        this._setGenePosition(chromosome0, mutations[2], point1, point2, point3, point2, point3, point1);
        this._setGenePosition(chromosome0, mutations[3], point1, point2, point3, point3, point1, point2);
        this._setGenePosition(chromosome0, mutations[4], point1, point2, point3, point3, point2, point1);

        BusSchoolEntity[] candidates = null;
        int minIndex = 0;
        //create 5 entities and choose the best one
        candidates = new BusSchoolEntity[index];
        double minFitness = Double.MAX_VALUE;
        double currentFitness;

        for (int i = 0; i < 5; i++) {
            int temp = index == 5 ? i : i + 1;
            candidates[temp] = new BusSchoolEntity(this.schoolBusConfiguration.getBusNumber(), mutations[temp]);
            currentFitness = this.calculateFitness(candidates[temp]);
            if (currentFitness < minFitness) {
                minFitness = currentFitness;
                minIndex = temp;
            }
        }
        return candidates[minIndex];
    }

    private void _setGenePosition(int[][] chromosome0, int[][] chromosome, int point1, int point2, int point3, int point11, int point31, int point21) {
        chromosome[point11][0] = chromosome0[point1][0];
        chromosome[point11][1] = chromosome0[point1][1];

        chromosome[point21][0] = chromosome0[point2][0];
        chromosome[point21][1] = chromosome0[point2][1];

        chromosome[point31][0] = chromosome0[point3][0];
        chromosome[point31][1] = chromosome0[point3][1];
    }

    // Select one entity randomly from the current population
    private BusSchoolEntity selectOneParent() {
        int index = new Random().nextInt(this.schoolBusConfiguration.getPopulationSize() - 1);
        return this.population.get(index);
    }

    // Select male and female parents from
    private RepresentativeEntity selectTwoParents() {
        int maleIndex, femaleIndex;
        maleIndex = new Random().nextInt((int) Math.round(this.schoolBusConfiguration.getSelectionRate() * this.schoolBusConfiguration.getPopulationSize()));
        do {
            femaleIndex = new Random().nextInt(this.population.size() - 1);
        } while (femaleIndex == maleIndex);
        BusSchoolEntity male = this.population.get(maleIndex);
        BusSchoolEntity female = this.population.get(femaleIndex);
        return new RepresentativeEntity(male, female);
    }

    private void generateInitialPopulation() {
        this.population = new ArrayList<>();

        while (this.population.size() < this.schoolBusConfiguration.getPopulationSize()) {

            List<Integer> busTemp = new ArrayList<>(this.buses);
            List<BusStop> busStopsTemp = new ArrayList<>(this.busStops);
            busStopsTemp = busStopsTemp.stream().filter(bst -> bst.getId() != 0).collect(Collectors.toList());

            BusSchoolEntity entity = new BusSchoolEntity(this.schoolBusConfiguration.getBusNumber(), this.busStops.size());
            int index = new Random().nextInt(busTemp.size() - 1);
            int bus = busTemp.get(index);
            busTemp.remove(index);
            while (busStopsTemp.size() > 0) {
                int i = busStopsTemp.size() > 1 ? new Random().nextInt(busStopsTemp.size() - 1) : 0;
                BusStop busStop = busStopsTemp.get(i);
                busStopsTemp.remove(i);
                if (busTemp.size() == 0){
                    busTemp = new ArrayList<>(this.buses);
                    busTemp = busTemp.stream().filter(bst -> bst != bus).collect(Collectors.toList());
                }
                if (!entity.assignBusToBusStop(bus, busStop, this.distanceMatrices,
                        this.schoolBusConfiguration.getVehicleCapacity(), this.schoolBusConfiguration.getMaxRiddingTime())) {
                    index = busTemp.size() > 1 ? new Random().nextInt(busTemp.size() - 1) : 0;
                    int busj = busTemp.get(index);
                    busTemp.remove(index);
                    entity.assignBusToBusStop(busj, busStop, this.distanceMatrices,
                            this.schoolBusConfiguration.getVehicleCapacity(), this.schoolBusConfiguration.getMaxRiddingTime());
                }
            }
            this.population.add(entity.encode());
        }
    }

}
