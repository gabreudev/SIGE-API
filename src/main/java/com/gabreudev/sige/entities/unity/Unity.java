package com.gabreudev.sige.entities.unity;

import com.gabreudev.sige.entities.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Table(name = "unities")
@Data
public class Unity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String address;

    @ManyToOne
    @JoinColumn(name = "preceptor_id")
    private User preceptor;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> availability;

    public Unity(String name, String address, User preceptor, Map<String, Object> availability) {
        this.name = name;
        this.address = address;
        this.preceptor = preceptor;
        this.availability = availability;
    }

    public Unity(Unity existing, String name, String address, User preceptor, Map<String, Object> availability) {
        this.id = existing.id;
        this.name = name != null ? name : existing.name;
        this.address = address != null ? address : existing.address;
        this.preceptor = preceptor != null ? preceptor : existing.preceptor;
        this.availability = availability != null ? availability : existing.availability;
    }
}
