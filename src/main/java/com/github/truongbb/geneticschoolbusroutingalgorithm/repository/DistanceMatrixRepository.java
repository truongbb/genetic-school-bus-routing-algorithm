package com.github.truongbb.geneticschoolbusroutingalgorithm.repository;

import com.github.truongbb.geneticschoolbusroutingalgorithm.entity.DistanceMatrix;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistanceMatrixRepository extends JpaRepository<DistanceMatrix, Integer> {
}
