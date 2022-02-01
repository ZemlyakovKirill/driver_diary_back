package com.example.workingwithtokens.sortingUtils;

import com.example.workingwithtokens.enums.CostTypes;
import com.google.gson.annotations.Expose;

public class VehicleCostType {
    @Expose
    private CostTypes type;
    @Expose
    private double value;
    @Expose
    private long count;

    public VehicleCostType(CostTypes type, double value, long count) {
        this.type = type;
        this.value = value;
        this.count = count;
    }
}
