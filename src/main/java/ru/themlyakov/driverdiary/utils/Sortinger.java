package ru.themlyakov.driverdiary.utils;

import org.springframework.data.domain.Sort;

import java.util.List;

public class Sortinger {
    public static<T extends Sortable<T>> void sort(List<T> unSortedData, String sortingParameter, Sort.Direction direction){
        if (direction == Sort.Direction.ASC) {
            unSortedData.sort((o1, o2) -> o1.parameterComparingTo(o2, sortingParameter));
        } else {
            unSortedData.sort((o1, o2) -> o2.parameterComparingTo(o1, sortingParameter));
        }
    }
}
