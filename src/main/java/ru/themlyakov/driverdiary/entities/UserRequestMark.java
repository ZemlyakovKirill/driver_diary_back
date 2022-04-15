package ru.themlyakov.driverdiary.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Entity
@Data
public class UserRequestMark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_mark_id")
    private Long userRequestMarkId;

    public UserRequestMark() {
    }

    public UserRequestMark(User user, RequestMark requestMark, boolean isTruth) {
        this.user = user;
        this.requestMark = requestMark;
        this.isTruth = isTruth;
    }

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = RequestMark.class)
    @JoinColumn(name = "request_id", nullable = false)
    private RequestMark requestMark;

    @Column(name="is_truth",nullable = false)
    @NotNull(message = "Статус не может быть пустым")
    private boolean isTruth;
}
