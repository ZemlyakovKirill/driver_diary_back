package ru.themlyakov.driverdiary.utils;

public interface Sortable<T> {
     int parameterComparingTo(T other,String parameter);

     String[] getComparableParameters();
}
