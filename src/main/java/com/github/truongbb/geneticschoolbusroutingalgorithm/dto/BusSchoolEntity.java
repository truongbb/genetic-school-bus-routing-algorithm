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
import java.util.List;
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
//    private List<BusStop> busStops;
//    private BusStopRepository busStopRepository;
//    public void display(){
//        this.busStops = busStopRepository.getAll();
//    }
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

    public BusSchoolEntity(Integer busNumber, int[][] chromosome) {

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
                    .filter(d -> d.getStartBusStop().equals(0) && d.getEndBusStop().equals(bst.getId()))
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
            currentDist = -1;
            for (BusStop bst : newBusStops) {
                BusStop finalK = k;
                DistanceMatrix distanceMatrixTmp = distanceMatrices
                        .stream()
                        .filter(d -> d.getStartBusStop().equals(finalK.getId()) && d.getEndBusStop().equals(bst.getId()))
                        .findFirst()
                        .orElse(null);
                if (ObjectUtils.isEmpty(distanceMatrixTmp)) {
                    continue;
                }
                if (distanceMatrixTmp.getDistance() > currentDist) {
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

    public BusSchoolEntity encode() {
        for (int bus = 0; bus < this.routes.size(); bus++) {
            for (int i = 0; i < this.routes.get(bus).getRoute().size(); i++) {
            }
        }
        return null;
    }

    public List<Double> getRouteLengths(List<DistanceMatrix> distanceMatrices, BusStop schoolStop) {
        return this.routes.stream().map(r -> {
            double dist = 0;
            for (int i = 0; i < r.getRoute().size(); i++) {
                // Get the distance of the two bus stops, if the current bus stop is the last one,
                // get the distance from it to the school. Then, sum up the distance with the current
                // total distance.
                BusStop busStop = r.getRoute().get(i);
                BusStop nextBusStop = r.getRoute().get(i + 1);
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

    public BusSchoolEntity fixBusCapacities(List<BusStop> busStops, Integer vehicleCapacity) {
        return null;
    }
}
