package com.example.workingwithtokens.entities;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
public class RequestMark {
    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @Expose
    @Column(name = "type", nullable = false, length = 50)
    @NotNull(message = "Mark type has to be not null")
    @Size(max = 50, message = "Mark type has to less than 50")
    private String type;

    @Expose
    @Column(name = "lat", nullable = false)
    @NotNull(message = "Latitude has to be not null")
    private Float lat;

    @Expose
    @Column(name = "lon", nullable = false)
    @NotNull(message = "Longitude has to be not null")
    private Float lon;

    @Expose
    @Column(name="name",nullable=false,length = 100)
    @Pattern(regexp = "[0-9A-zА-я]{1,100}",message = "Name length has to be between [1,100]")
    @NotNull(message = "Name has to be not null")
    private String name;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Request issuer has to be not null")
    private User user;

    public RequestMark(String type, Float lat, Float lon, String name, User user) {
        this.id = id;
        this.type = type;
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.user = user;
    }

    public RequestMark() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "RequestMark{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", name='" + name + '\'' +
                ", user=" + user +
                '}';
    }
}
