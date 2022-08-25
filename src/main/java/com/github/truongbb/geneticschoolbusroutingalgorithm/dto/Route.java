package com.github.truongbb.geneticschoolbusroutingalgorithm.dto;

import com.github.truongbb.geneticschoolbusroutingalgorithm.entity.BusStop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Route {

    List<BusStop> route;

    public Route() {
        this.route = new ArrayList<>();
    }

}
