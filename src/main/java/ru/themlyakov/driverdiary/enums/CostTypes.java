package ru.themlyakov.driverdiary.enums;

public enum CostTypes {
    REFUELING,
    WASHING,
    SERVICE,
    OTHER;

    public static CostTypes fromOrdinal(int ordinal) {
        switch (ordinal) {
            case 0:
                return REFUELING;
            case 1:
                return WASHING;
            case 2:
                return SERVICE;
            case 3:
            default:
                return OTHER;
        }
    }


}
