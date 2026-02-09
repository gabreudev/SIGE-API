package com.gabreudev.sige.entities.shift;

import com.gabreudev.sige.entities.report.ShiftReport;
import com.gabreudev.sige.entities.unity.Unity;
import com.gabreudev.sige.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    @JoinColumn(name = "unity_id", nullable = true)
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

    @OneToOne(mappedBy = "shift", cascade = CascadeType.ALL, orphanRemoval = true)
    private ShiftReport report;
}
