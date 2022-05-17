package ru.themlyakov.driverdiary.entities;

import com.google.gson.annotations.Expose;
import ru.themlyakov.driverdiary.enums.SearchTypeMarks;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class RequestMark {
    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @Expose
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @NotNull(message="Тип марки не может быть нулевой")
    private SearchTypeMarks type;

    @Expose
    @Column(name = "lat", nullable = false)
    @NotNull(message="Широта не может быть нулевой")
    private Float lat;

    @Expose
    @Column(name = "lon", nullable = false)
    @NotNull(message="Долгота не может быть нулевой")
    private Float lon;

    @Expose
    @Column(name="name",nullable=false,length = 100)
    @Pattern(regexp = "[0-9A-zА-я]{1,100}",message = "Длина наименования марки должна быть в диапазоне от 1 до 100 символов")
    @NotNull(message = "Наименование не может быть нулевым")
    private String name;


    public RequestMark(SearchTypeMarks type, Float lat, Float lon, String name) {
        this.type = type;
        this.lat = lat;
        this.lon = lon;
        this.name = name;
    }

    public RequestMark() {

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SearchTypeMarks getType() {
        return type;
    }

    public void setType(SearchTypeMarks type) {
        this.type = type;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getLon() {
        return lon;
    }

    public void setLon(Float lon) {
        this.lon = lon;
    }


    @Override
    public String toString() {
        return "RequestMark{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", name='" + name + '\'' +
                '}';
    }
}
