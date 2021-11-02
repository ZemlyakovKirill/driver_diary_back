package com.example.workingwithtokens.entities;

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
    @Column(name = "description", nullable = false, length = 100)
    @NotNull(message = "Description has to be not null")
    @Size(max = 100, message = "Description has to be less than 100")
    private String description;

    @Expose
    @Column(name="value")
    private Float value;

    @Expose
    @Column(name="end_date",nullable = false)
    @NotNull(message = "Finish date has to be not null")
    private Date endDate;

    @Expose
    @Column(name="is_cost",nullable = false)
    @NotNull(message = "Is cost has to be not null")
    private boolean isCost;

    @Expose
    @Column(name="is_completed",nullable = false)
    @NotNull(message = "Finish date has to be not null")
    private boolean isCompleted;

    @ManyToOne
    @JoinColumn(name="user_id",nullable = false)
    @NotNull(message = "User has to be not null")
    private User user;
}
