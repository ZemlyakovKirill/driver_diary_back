package com.example.workingwithtokens.entities;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "user_vehicle")
public class UserVehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ownership_id")
    private int id;

    @Expose
    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "vehicle_id",nullable = false)
    @NotNull(message = "Id транспортного средства не может быть нулевым")
    private Vehicle vehicle;

    @ManyToOne(fetch=FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id",nullable = false)
    @NotNull(message="Id пользователя не может быть нулевым")
    private User user;

    @Expose
    @OneToMany(mappedBy = "userVehicle",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<VehicleCosts> vehicleCosts;


    public UserVehicle(Vehicle vehicle, User user) {
        this.vehicle = vehicle;
        this.user = user;
    }

    public UserVehicle() {
    }

    public UserVehicle(int id, Vehicle vehicle, User user) {
        this.id = id;
        this.vehicle = vehicle;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<VehicleCosts> getVehicleCosts() {
        return vehicleCosts;
    }

    public void setVehicleCosts(Set<VehicleCosts> vehicleCosts) {
        this.vehicleCosts = vehicleCosts;
    }
}
