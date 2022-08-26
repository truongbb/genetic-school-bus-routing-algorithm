package com.github.truongbb.geneticschoolbusroutingalgorithm.repository;

import com.github.truongbb.geneticschoolbusroutingalgorithm.entity.BusStop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusStopRepository extends JpaRepository<BusStop, Integer> {
}
