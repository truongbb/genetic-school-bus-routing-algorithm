package com.github.truongbb.geneticschoolbusroutingalgorithm.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "BUS_STOP")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BusStop implements Serializable {

    @Id
    @Column(nullable = false)
    Integer id;

    @Column
    Double latitude;

    @Column
    Double longitude;

    @Column(name = "number_of_student", nullable = false)
    Integer numberOfStudent;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusStop busStop = (BusStop) o;
        return id.equals(busStop.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
