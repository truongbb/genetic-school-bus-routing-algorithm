package com.github.truongbb.geneticschoolbusroutingalgorithm.dto;

import com.github.truongbb.geneticschoolbusroutingalgorithm.entity.BusStop;
import com.github.truongbb.geneticschoolbusroutingalgorithm.entity.DistanceMatrix;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BusSchoolEntity {

    List<Route> routes; // danh sách các tuyến xe
    int[][] chromosome; // nhiễm sắc thể phục vụ việc tối ưu tìm kiếm và đi lai ghép, đột biến
    boolean isElite; // thực thể này có phải là đáp án tối ưu, tốt nhất chưa
    double Fitness;

    /**
     * Cấu trúc của nhiễm sắc thể chromosome như sau:
     * Mảng 2 chiều [n][m] với
     * n = số điểm dừng (bus stop number)
     * m = 2 (vị trí đầu tiên lưu id tuyến, vị trí thứ 2 lưu vị trí của điểm dừng trong tuyến đó)
     * <p>
     * ==> Mảng 2 chiều chromosome mang ý nghĩa lưu trữ xem điểm dừng đó nằm ở tuyến nào, vị trí nào trong tuyến
     */
    public BusSchoolEntity(Integer busNumber, Integer busStopNumber) {

        this.routes = new ArrayList<>();
        this.chromosome = new int[busStopNumber][];

        for (int i = 0; i < busNumber; i++) {
            this.routes.add(new Route());
        }

        for (int i = 0; i < busStopNumber; i++) {
            this.chromosome[i] = new int[]{0, 0};
        }
    }

    public BusSchoolEntity(Integer busNumber, int[][] chromosome, List<BusStop> busStops) {
        this.routes = new ArrayList<>();
        this.chromosome = chromosome;

        // Initiate each route with a list of N genes (N is the number of bus stops)
        for (int i = 0; i < busNumber; i++) {
            ArrayList<BusStop> tempRoute = new ArrayList<>(chromosome.length);
            for (int j = 0; j < chromosome.length; j++) {
                tempRoute.add(busStops.get(0));
            }
            this.routes.add(new Route(tempRoute));
        }

        //Insert the bus sop (i+1) to the rout marked by the gene chromosome[i][0] at chromosome[i][1] the position
        for (int i = 0; i < this.chromosome.length; i++) {
            int tempI = i + 1;
            BusStop busStop = busStops.stream().filter(t -> t.getId().equals(tempI)).findFirst().orElse(null);
            this.routes.get(this.chromosome[i][0] - 1).getRoute().add(this.chromosome[i][1], busStop);
        }

        // Remove all empty genes
        for (int i = 0; i < busNumber; i++) {
            // TODO - create new entity
            List<BusStop> route = this.routes.get(i).getRoute();
            List<BusStop> removeList = route.stream().filter(s -> s.getId() == 0).collect(Collectors.toList());
            this.routes.get(i).getRoute().removeAll(removeList);
        }
    }

    public int[][] getChromosome() {
        int[][] copy = new int[this.chromosome.length][];
        for (int i = 0; i < this.chromosome.length; i++) {
            copy[i] = new int[]{this.chromosome[i][0], this.chromosome[i][1]};
        }
        return copy;
    }

    public boolean assignBusToBusStop(Integer bus, BusStop busStop, List<DistanceMatrix> distanceMatrices, Integer vehicleCapacity, Integer maxRiddingTime) {
        Route route = this.routes.get(bus - 1);
        List<BusStop> busStops = route.getRoute();
        int currentCapacity = busStops.stream().mapToInt(BusStop::getNumberOfStudent).sum();

        // do not add this bus stop to the current route if the bus is almost full
        if (currentCapacity + busStop.getNumberOfStudent() > vehicleCapacity) {
            return false;
        }
        List<BusStop> newBusStops = new ArrayList<>(busStops);
        newBusStops.add(busStop);

        List<BusStop> sortedRoute = new ArrayList<>();

        // reorder the new list of bus stop to calculate the route length
        BusStop k = null, k1 = null;
        double currentDist = -1;

        // find the bus stop that is furthest away from the school
        for (BusStop bst : newBusStops) {
            DistanceMatrix distanceMatrix = distanceMatrices
                    .stream()
                    .filter(d -> d.getStartBusStop().getId().equals(0) && d.getEndBusStop().getId().equals(bst.getId()))
                    .findFirst()
                    .orElse(null);
            if (ObjectUtils.isEmpty(distanceMatrix)) {
                continue;
            }
            if (distanceMatrix.getDistance() > currentDist) {
                k = bst;
                currentDist = distanceMatrix.getDistance();
            }
        }

        double totalDist = 0;
        sortedRoute.add(k);
        newBusStops.remove(k);
        while (newBusStops.size() > 0) {
            // choose the next bus stop to be the nearest one to k
            currentDist = 99999999;
            for (BusStop bst : newBusStops) {
                BusStop finalK = k;
                DistanceMatrix distanceMatrixTmp = distanceMatrices
                        .stream()
                        .filter(d -> d.getStartBusStop().getId().equals(finalK.getId()) && d.getEndBusStop().getId().equals(bst.getId()))
                        .findFirst()
                        .orElse(null);
                if (ObjectUtils.isEmpty(distanceMatrixTmp)) {
                    continue;
                }
                if (distanceMatrixTmp.getDistance() < currentDist) {
                    k1 = bst;
                    currentDist = distanceMatrixTmp.getDistance();
                }
            }
            sortedRoute.add(k1);
            newBusStops.remove(k1);
            BusStop finalK = k;
            BusStop finalK2 = k1;
            DistanceMatrix distanceMatrix = distanceMatrices
                    .stream()
                    .filter(d ->
                            d.getStartBusStop().getId().equals(finalK.getId())
                                    && d.getEndBusStop().getId().equals(finalK2.getId())
                    )
                    .findFirst()
                    .orElse(null);
            totalDist += ObjectUtils.isEmpty(distanceMatrix) ? 0 : distanceMatrix.getDistance();
            k = k1;

            if (sortedRoute.size() > busStops.size() + 1) {
                System.out.println("ERRORRRRRRRRRRRRRR");
            }
        }

        this.routes.get(bus - 1).setRoute(sortedRoute);
        return true;

    }

    // Encode the current information into chromosome
    public BusSchoolEntity encode() {
        for (int bus = 0; bus < this.routes.size(); bus++) {
            Route route = this.routes.get(bus);
            for (int i = 0; i < route.getRoute().size(); i++) {
                BusStop busStops = route.getRoute().get(i);
                Integer busStop = busStops.getId();

                // the bus stop is 1-index
                // the bus is 0-index while the representation is 1-index
                this.chromosome[busStop - 1][0] = bus + 1;
                this.chromosome[busStop - 1][1] = i;
            }
        }
        return this;
    }

    public List<Double> getRouteLengths(List<DistanceMatrix> distanceMatrices, BusStop schoolStop) {
        return this.routes.stream().map(r -> {
            double dist = 0;
            for (int i = 0; i < r.getRoute().size(); i++) {
                // Get the distance of the two bus stops, if the current bus stop is the last one,
                // get the distance from it to the school. Then, sum up the distance with the current
                // total distance.
                BusStop busStop = r.getRoute().get(i);
                BusStop nextBusStop = i + 1 >= r.getRoute().size() ? schoolStop : r.getRoute().get(i + 1);
                DistanceMatrix distanceMatrix = null;
                int finalI = i;
                distanceMatrix = distanceMatrices
                        .stream()
                        .filter(d -> d.getStartBusStop().getId().equals(busStop.getId()) &&
                                finalI + 1 >= r.getRoute().size() ? d.getEndBusStop().getId().equals(schoolStop.getId()) : d.getEndBusStop().getId().equals(nextBusStop.getId())
                        )
                        .findFirst()
                        .orElse(null);
                dist += !ObjectUtils.isEmpty(distanceMatrix) ? distanceMatrix.getDistance() : 0;
            }
            return dist;
        }).collect(Collectors.toList());
    }

    public BusSchoolEntity fixBusCapacities(List<BusStop> busStops, Integer busCapacity) {
        List<BusStop> redundantBusStops = new ArrayList<>();
        int currentCapacity, currentIndex;

        // The tmpCapacities hold the current capacity of each bus
        Map<Integer, Integer> tmpCapacities = new HashMap<>();

        // traverse through routes and let all bus capacities smaller than the limitation
        for (int i = 0; i < this.routes.size(); i++) {
            Route route = this.routes.get(i);
            currentCapacity = 0;
            currentIndex = 0;
            List<BusStop> bs = route.getRoute();
            for (int j = 0; j < bs.size(); j++) {
                Integer numberOfStudent = busStops.get(bs.get(j).getId()).getNumberOfStudent();
                currentCapacity += numberOfStudent;
                if (currentCapacity > busCapacity) {
                    currentIndex = j - 1;
                    tmpCapacities.put(i, currentCapacity - numberOfStudent);
                    break;
                }
            }
            tmpCapacities.put(i, currentCapacity);

            if (currentIndex > 0) {
                for (int j = route.getRoute().size() - 1; j > currentIndex; j--) {
                    redundantBusStops.add(route.getRoute().get(j));
                    route.getRoute().remove(j);
                }
            }
        }
        // add redundant bus stops to buses with lowest loads
        for (BusStop busStop : redundantBusStops) {
            int routeIndex = 0;
            int minLoad = Integer.MAX_VALUE;
            for (int i = 0; i < tmpCapacities.size(); i++) {
                Integer currentLoad = tmpCapacities.get(i);
                if (currentLoad < minLoad) {
                    minLoad = currentLoad;
                    routeIndex = i;
                }
            }

            Route route = this.routes.get(routeIndex);
            route.getRoute().add(busStop);
            int newCapacity = tmpCapacities.get(routeIndex) + busStops.get(busStop.getId()).getNumberOfStudent();
            tmpCapacities.put(routeIndex, newCapacity);
        }

        return this;
    }

}
