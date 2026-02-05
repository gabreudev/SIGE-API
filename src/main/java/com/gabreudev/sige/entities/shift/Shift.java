package com.gabreudev.sige.entities.shift;

import com.gabreudev.sige.entities.unity.Unity;
import com.gabreudev.sige.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shifts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "unity_id", nullable = false)
    private Unity unity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShiftType type;

    @Column(nullable = false)
    private Integer hours;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShiftPeriod period;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Boolean validated = false;

    @Column(name = "validation_date")
    private LocalDateTime validationDate;

    @ManyToOne
    @JoinColumn(name = "validated_by_user_id")
    private User validatedBy;
}
