package com.example.workingwithtokens.entities;


import com.google.gson.annotations.Expose;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;//идентификатор

    @Expose
    @Column(unique = true, name = "username", length = 50, nullable = false)
    @Pattern(regexp = "[a-z0-9]{3,50}", message = "Username has to be between 3 and 50 and only latin symbols and arabic numerals")
    @NotNull(message = "Username has to be not null")
    private String username;//никнейм

    @Column(name = "password", nullable = false)
    @NotNull(message = "Password has to be not null")
    @Size(min = 6, max = 255, message = "Password has to be between 5 and 255")
    private String password;//пароль

    @Expose
    @Column(name = "active", nullable = false)
    @NotNull(message = "Active has to be not null")
    private Boolean active;//активный пользователь

    @Expose
    @Column(name="is_vk",nullable = false)
    @NotNull(message = "Is vk has to be not null")
    private Boolean isVk;

    @Expose
    @Column(name="is_google",nullable = false)
    @NotNull(message = "Is google has to be not null")
    private Boolean isGoogle;

    @Expose
    @Column(name = "email", unique = true, nullable = false)
    @Pattern(regexp = "[a-z0-9._-]{2,40}+@[a-z0-9]{2,7}+.[a-z0-9]{2,5}", message = "Email has to be like example@exmp.com")
    @Size(min = 7, max = 100, message = "Email out of size [7,100]")
    @NotNull(message = "Email has to be not null")
    private String email;

    @Expose
    @Column(name = "telnum", length = 20)
    @Size(min = 10, max = 20, message = "Phone number out of size[10,20]")
    private String telnum;

    @Expose
    @Column(name = "last_name", nullable = false, length = 100)
    @Size(max = 100, message = "Last Name has to be less than 100 symbols")
    @Pattern(regexp = "[A-ZА-Я][a-zа-я]{1,}", message = "Last Name has to be like Johnson")
    @NotNull(message = "Last Name has to be not null")
    private String lastName;

    @Expose
    @Column(name = "first_name", nullable = false, length = 100)
    @Size(max = 100, message = "First Name has to be less than 100 symbols")
    @Pattern(regexp = "[A-ZА-Я][a-zа-я]{1,}", message = "First Name has to be like John")
    @NotNull(message = "First Name has to be not null")
    private String firstName;

    @Expose
    @Column(name = "roles", nullable = false)
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "user_authorities",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id"))
    @NotNull
    private Set<Authority> authorities;

    public Set<UserNote> getNotes() {
        return notes;
    }

    public void setNotes(Set<UserNote> notes) {
        this.notes = notes;
    }

    @Expose
    @OneToMany(mappedBy = "user")
    private Set<UserNote> notes;


    @Expose
    @OneToMany(mappedBy = "user")
    private Set<UserVehicle> userVehicles;

    public User(String username, String password, Boolean active, String email, String lastName, String firstName,Boolean isVk,Boolean isGoogle, Set<Authority> authorities) {
        this.username = username;
        this.password = password;
        this.active = active;
        this.email = email;
        this.isVk=isVk;
        this.isGoogle=isGoogle;
        this.lastName = lastName;
        this.firstName = firstName;
        this.authorities = authorities;
    }

    public User(String username, String password, Boolean active, String email, String telnum, String lastName, String firstName,Boolean isVk,Boolean isGoogle, Set<Authority> authorities) {
        this.username = username;
        this.password = password;
        this.active = active;
        this.email = email;
        this.isVk=isVk;
        this.isGoogle=isGoogle;
        this.telnum = telnum;
        this.lastName = lastName;
        this.firstName = firstName;
        this.authorities = authorities;
    }

    public Set<Vehicle> getVehicles() {
        Set<Vehicle> vehicles = new HashSet<>();
        userVehicles.stream().forEach(cv->vehicles.add(cv.getVehicle()));
        return vehicles;
    }

    public User(Long id, String username, String password, Boolean active, String email, String telnum, String lastName, String firstName,Boolean isVk,Boolean isGoogle, Set<Authority> authorities, Set<UserVehicle> userVehicles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.active = active;
        this.email = email;
        this.isVk=isVk;
        this.isGoogle=isGoogle;
        this.telnum = telnum;
        this.lastName = lastName;
        this.firstName = firstName;
        this.authorities = authorities;
        this.userVehicles = userVehicles;
    }

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelnum() {
        return telnum;
    }

    public void setTelnum(String telnum) {
        this.telnum = telnum;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public Set<UserVehicle> getUserVehicles() {
        return userVehicles;
    }

    public void setUserVehicles(Set<UserVehicle> userVehicles) {
        this.userVehicles = userVehicles;
    }

    public Boolean getVk() {
        return isVk;
    }

    public void setVk(Boolean vk) {
        isVk = vk;
    }

    public Boolean getGoogle() {
        return isGoogle;
    }

    public void setGoogle(Boolean google) {
        isGoogle = google;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", active=" + active +
                ", isVk=" + isVk +
                ", isGoogle=" + isGoogle +
                ", email='" + email + '\'' +
                ", telnum='" + telnum + '\'' +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", authorities=" + authorities +
                ", notes=" + notes +
                ", userVehicles=" + userVehicles +
                '}';
    }
}
