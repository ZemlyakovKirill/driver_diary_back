package ru.themlyakov.driverdiary.utils;

import ru.themlyakov.driverdiary.enums.CostTypes;
import com.google.gson.annotations.Expose;

public class VehicleCostType {
    @Expose
    private CostTypes type;
    @Expose
    private double value;

    public VehicleCostType(CostTypes type, double value) {
        this.type = type;
        this.value = value;
    }
}
