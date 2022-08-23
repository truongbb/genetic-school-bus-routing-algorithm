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

}
