package com.example.workingwithtokens.sortingUtils;

import com.example.workingwithtokens.entities.Vehicle;
import com.example.workingwithtokens.entities.VehicleCosts;

import java.util.*;
import java.util.stream.Collectors;

public class Sortinger {

    public static <T extends Comparable<T>> Collection<T> sort(Class<T> object,Collection<T> collection,String sortBy) {
        Objects.requireNonNull(collection);
        if (object.isAssignableFrom(VehicleCosts.class)){
            switch (sortBy){
                case "Date<":
                    return collection.stream().sorted(Comparator.comparing(a -> ((VehicleCosts) a).getDate())).collect(Collectors.toCollection(LinkedHashSet::new));
                case "Date>":
                    return collection.stream().sorted(Comparator.comparing(a -> ((VehicleCosts) a).getDate()).reversed()).collect(Collectors.toCollection(LinkedHashSet::new));
                case "Value<":
                    return collection.stream().sorted(Comparator.comparing(a -> ((VehicleCosts) a).getValue())).collect(Collectors.toCollection(LinkedHashSet::new));
                case "Value>":
                    return collection.stream().sorted(Comparator.comparing(a -> ((VehicleCosts) a).getValue(),Comparator.reverseOrder())).collect(Collectors.toCollection(LinkedHashSet::new));
                case "Vehicle<":
                    return collection.stream().sorted(Comparator.comparing(a -> ((VehicleCosts) a).getUserVehicle().getVehicle().getMark())).collect(Collectors.toCollection(LinkedHashSet::new));
                case "Vehicle>":
                    return collection.stream().sorted(Comparator.comparing(a -> ((VehicleCosts) a).getUserVehicle().getVehicle().getMark(),Comparator.reverseOrder())).collect(Collectors.toCollection(LinkedHashSet::new));
                case "Type<":
                    return collection.stream().sorted(Comparator.comparing(a -> ((VehicleCosts) a).getType())).collect(Collectors.toCollection(LinkedHashSet::new));
                case "Type>":
                    return collection.stream().sorted(Comparator.comparing(a -> ((VehicleCosts) a).getType(),Comparator.reverseOrder())).collect(Collectors.toCollection(LinkedHashSet::new));

            }
        }else if(object.isAssignableFrom(Vehicle.class)){
            switch(sortBy){
                case "Mark<":
                    return collection.stream().sorted(Comparator.comparing(a -> ((Vehicle) a).getMark())).collect(Collectors.toCollection(LinkedHashSet::new));
                case "Mark>":
                    return collection.stream().sorted(Comparator.comparing(a -> ((Vehicle) a).getMark(),Comparator.reverseOrder())).collect(Collectors.toCollection(LinkedHashSet::new));
                case "Model<":
                    return collection.stream().sorted(Comparator.comparing(a -> ((Vehicle) a).getModel())).collect(Collectors.toCollection(LinkedHashSet::new));
                case "Model>":
                    return collection.stream().sorted(Comparator.comparing(a -> ((Vehicle) a).getModel(),Comparator.reverseOrder())).collect(Collectors.toCollection(LinkedHashSet::new));

            }
        }
        return collection;
    }
}
