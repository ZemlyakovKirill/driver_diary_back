package ru.themlyakov.driverdiary.entities;

import com.google.gson.annotations.Expose;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.themlyakov.driverdiary.enums.CostTypes;
import ru.themlyakov.driverdiary.utils.Sortable;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "vehicle_costs")
public class VehicleCosts implements Sortable<VehicleCosts> {
    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cost_id")
    private Long costId;

    @Expose
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @NotNull(message = "Тип расхода не может быть нулевым")
    private CostTypes type;

    @Expose
    @Column(name = "value", nullable = false)
    @Min(value = 0,message = "Значение расхода не может быть отрицательным")
    @NotNull(message = "Значение расхода не может быть нулевым")
    private Float value;

    @Expose
    @Column(name = "date", nullable = false)
    @NotNull(message = "Дата расхода не может быть нулевой")
    private Date date = new Date();

    @Expose
    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH})
    @JoinColumn(name = "ownership_id", nullable = false)
    @NotNull(message = "Расход должен принадлежать пользователю")
    private UserVehicle userVehicle;



    public VehicleCosts() {
    }

    public VehicleCosts(Long costId, CostTypes type, Float value, Date date, UserVehicle userVehicle) {
        this.costId = costId;
        this.type = type;
        this.value = value;
        this.date = date;
        this.userVehicle = userVehicle;
    }

    public VehicleCosts( CostTypes type, Float value, Date date, UserVehicle userVehicle) {
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

    public CostTypes getType() {
        return type;
    }

    public void setType(CostTypes type) {
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
    public int parameterComparingTo(VehicleCosts other, String parameter) {
        switch (parameter){
            case "date":
                return date.compareTo(other.date);
            case "type":
                return type.compareTo(other.type);
            case "value":
                return value.compareTo(other.value);
            default:
                return 0;
        }
    }

    @Override
    public String[] getComparableParameters() {
        return new String[]{"date","type","value"};
    }
}
