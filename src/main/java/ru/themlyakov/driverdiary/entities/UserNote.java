package ru.themlyakov.driverdiary.entities;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@Entity
@Table(name="notes")
@AllArgsConstructor
@NoArgsConstructor
public class UserNote {
    @Expose
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="note_id")
    private Long id;

    @Expose
    @Column(name = "description", length = 100)
    @Size(max = 100, message = "Длина описания должна быть меньше или равна 100 символам")
    private String description;

    @Expose
    @Column(name="value")
    private Float value;

    @Expose
    @Column(name="end_date",nullable = false)
    @NotNull(message = "Дедлайн должен не может быть нулевым")
    private Date endDate;

    @Expose
    @Column(name="is_cost",nullable = false)
    @NotNull(message = "Поле является ли запись расходом не может быть нулевой")
    private boolean isCost;

    @Expose
    @Column(name="is_completed",nullable = false)
    @NotNull(message = "Поле выполнена ли заметка не может быть нулевым")
    private boolean isCompleted;

    @Expose
    @Column(name="type")
    private String type;

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH})
    @JoinColumn(name="user_id",nullable = false)
    @NotNull(message = "Идентификатор пользователя не может быть нулевым")
    private User user;

    @Expose
    @ManyToOne
    @JoinColumn(name="ownership_id")
    private UserVehicle userVehicle;

    public UserNote(String description, Float value, Date endDate, boolean isCost, boolean isCompleted, String typeCost, User user, UserVehicle userVehicle) {
        this.description = description;
        this.value = value;
        this.endDate = endDate;
        this.isCost = isCost;
        this.isCompleted = isCompleted;
        this.type = typeCost;
        this.user = user;
        this.userVehicle = userVehicle;
    }

    public UserNote(String description, Float value, Date endDate, boolean isCost, boolean isCompleted, String typeCost, User user) {
        this.description = description;
        this.value = value;
        this.endDate = endDate;
        this.isCost = isCost;
        this.isCompleted = isCompleted;
        this.type = typeCost;
        this.user = user;
    }

    public UserNote(String description, Date endDate, boolean isCost, boolean isCompleted, User user) {
        this.description = description;
        this.endDate = endDate;
        this.isCost = isCost;
        this.isCompleted = isCompleted;
        this.user = user;
    }


    public UserNote(Long id, String description, Date endDate, boolean isCost, boolean isCompleted, User user) {
        this.id=id;
        this.description = description;
        this.endDate = endDate;
        this.isCost = isCost;
        this.isCompleted = isCompleted;
        this.user = user;
    }
}
