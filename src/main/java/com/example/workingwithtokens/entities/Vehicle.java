package com.example.workingwithtokens.entities;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "vehicles")
public class Vehicle implements Comparable<Vehicle> {
    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Long id;

    @Expose
    @Column(name = "mark", length = 100, nullable = false)
    @Size(max = 100, message = "Mark has to be less than 100")
    @NotNull(message = "Mark has to be not null")
    private String mark;

    @Expose
    @Column(name = "model", length = 100, nullable = false)
    @Size(max = 100, message = "Model has to be less than 100")
    @NotNull(message = "Model to be not null")
    private String model;

    @Expose
    @Column(name = "generation", length = 20)
    @Size(max = 20, message = "Generation has to be less than 20")
    private String generation;

    @Expose
    @Column(name = "consumption_city", nullable = false)
    @Min(value=0,message = "Consumption City has to be positive")
    @NotNull(message = "Consumption City has to be not null")
    private Float consumptionCity;

    @Expose
    @Column(name = "consumption_route", nullable = false)
    @Min(value=0,message = "Consumption Route has to be positive")
    @NotNull(message = "Consumption Route has to be not null")
    private Float consumptionRoute;

    @Expose
    @Column(name = "consumption_mixed", nullable = false)
    @Min(value=0,message = "Consumption Mixed has to be positive")
    @NotNull(message = "Consumption Mixed has to be not null")
    private Float consumptionMixed;

    @Expose
    @Column(name = "fuel_capacity", nullable = false)
    @Min(value=0,message = "Fuel Capacity has to be positive")
    @NotNull(message = "Fuel Capacity has to be not null")
    private Float fuelCapacity;

    @Expose
    @Column(name = "license_plate_number", length = 20)
    @Size(max = 20, message = "License Plate Number has to be less than 20")
    private String licensePlateNumber;

    public Vehicle(String mark, String model, Float consumptionCity, Float consumptionRoute, Float consumptionMixed, Float fuelCapacity) {
        this.mark = mark;
        this.model = model;
        this.consumptionCity = consumptionCity;
        this.consumptionRoute = consumptionRoute;
        this.consumptionMixed = consumptionMixed;
        this.fuelCapacity = fuelCapacity;
    }

    public Vehicle(String mark, String model, String generation, Float consumptionCity, Float consumptionRoute, Float consumptionMixed, Float fuelCapacity) {
        this.mark = mark;
        this.model = model;
        this.generation = generation;
        this.consumptionCity = consumptionCity;
        this.consumptionRoute = consumptionRoute;
        this.consumptionMixed = consumptionMixed;
        this.fuelCapacity = fuelCapacity;
    }

    public Vehicle(String mark, String model, Float consumptionCity, Float consumptionRoute, Float consumptionMixed, Float fuelCapacity, String licensePlateNumber) {
        this.mark = mark;
        this.model = model;
        this.consumptionCity = consumptionCity;
        this.consumptionRoute = consumptionRoute;
        this.consumptionMixed = consumptionMixed;
        this.fuelCapacity = fuelCapacity;
        this.licensePlateNumber = licensePlateNumber;
    }

    public Vehicle(Long id, String mark, String model, String generation, Float consumptionCity, Float consumptionRoute, Float consumptionMixed, Float fuelCapacity, String licensePlateNumber) {
        this.id = id;
        this.mark = mark;
        this.model = model;
        this.generation = generation;
        this.consumptionCity = consumptionCity;
        this.consumptionRoute = consumptionRoute;
        this.consumptionMixed = consumptionMixed;
        this.fuelCapacity = fuelCapacity;
        this.licensePlateNumber = licensePlateNumber;
    }

    public Vehicle() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getGeneration() {
        return generation;
    }

    public void setGeneration(String generation) {
        this.generation = generation;
    }

    public Float getConsumptionCity() {
        return consumptionCity;
    }

    public void setConsumptionCity(Float consumptionCity) {
        this.consumptionCity = consumptionCity;
    }

    public Float getConsumptionRoute() {
        return consumptionRoute;
    }

    public void setConsumptionRoute(Float consumptionRoute) {
        this.consumptionRoute = consumptionRoute;
    }

    public Float getConsumptionMixed() {
        return consumptionMixed;
    }

    public void setConsumptionMixed(Float consumptionMixed) {
        this.consumptionMixed = consumptionMixed;
    }

    public Float getFuelCapacity() {
        return fuelCapacity;
    }

    public void setFuelCapacity(Float fuelCapacity) {
        this.fuelCapacity = fuelCapacity;
    }

    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    public void setLicensePlateNumber(String licensePlateNumber) {
        this.licensePlateNumber = licensePlateNumber;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", mark='" + mark + '\'' +
                ", model='" + model + '\'' +
                ", generation='" + generation + '\'' +
                ", consumptionCity=" + consumptionCity +
                ", consumptionRoute=" + consumptionRoute +
                ", consumptionMixed=" + consumptionMixed +
                ", fuelCapacity=" + fuelCapacity +
                ", licensePlateNumber='" + licensePlateNumber + '\'' +
                '}';
    }

    @Override
    public int compareTo(Vehicle o) {
        return 0;
    }
}
