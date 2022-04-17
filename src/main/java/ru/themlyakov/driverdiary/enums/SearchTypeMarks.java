package ru.themlyakov.driverdiary.enums;

public enum SearchTypeMarks {
    GAS("Заправка"),
    WASH("Автомойка"),
    SERVICE("Автосервис"),
    METHANE("Заправка метан"),
    CHARGE("Станция зарядки");

    private String searchValue;
    SearchTypeMarks(String searchValue){
        this.searchValue=searchValue;
    }

    public String search(){
        return searchValue;
    }
}
