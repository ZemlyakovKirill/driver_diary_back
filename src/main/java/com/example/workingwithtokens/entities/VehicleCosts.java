package com.example.workingwithtokens.entities;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "vehicle_costs")
public class VehicleCosts implements Comparable<VehicleCosts> {
    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cost_id")
    private Long costId;

    @Expose
    @Column(name = "type", length = 100, nullable = false)
    @NotNull(message = "Тип расхода не может быть нулевым")
    private String type;

    @Expose
    @Column(name = "value", nullable = false)
    @Min(value = 0,message = "Значение расхода не может быть отрицательным")
    @NotNull(message = "Значение расхода не может быть нулевым")
    private Float value;

    @Expose
    @Column(name = "date", nullable = false)
    private Date date = new Date();

    @ManyToOne
    @JoinColumn(name = "ownership_id", nullable = false)
    @NotNull(message = "Расход должен принадлежать пользователю")
    private UserVehicle userVehicle;

    public VehicleCosts() {
    }

    public VehicleCosts(Long costId, String type, Float value, Date date, UserVehicle userVehicle) {
        this.costId = costId;
        this.type = type;
        this.value = value;
        this.date = date;
        this.userVehicle = userVehicle;
    }

    public VehicleCosts( String type, Float value, Date date, UserVehicle userVehicle) {
        this.type = type;
        this.value = value;
        this.date = date;
        this.userVehicle = userVehicle;
    }

    public Long getCostId() {
        return costId;
    }

    public void setCostId(Long costId) {
        this.costId = costId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public UserVehicle getUserVehicle() {
        return userVehicle;
    }

    public void setUserVehicle(UserVehicle userVehicle) {
        this.userVehicle = userVehicle;
    }

    @Override
    public String toString() {
        return "VehicleCosts{" +
                "costId=" + costId +
                ", type='" + type + '\'' +
                ", value=" + value +
                ", date=" + date +
                '}';
    }

    @Override
    public int compareTo(VehicleCosts o) {
        return 0;
    }

}
