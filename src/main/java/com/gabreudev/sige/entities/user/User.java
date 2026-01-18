package com.gabreudev.sige.entities.user;

import com.gabreudev.sige.entities.user.dto.AdminUpdateDTO;
import com.gabreudev.sige.entities.user.dto.PreceptorUpdateDTO;
import com.gabreudev.sige.entities.user.dto.StudentUpdateDTO;
import com.gabreudev.sige.entities.user.dto.SupervisorUpdateDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Entity
@NoArgsConstructor
@Table(name = "users")
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String registration; // student as matricula | supervisor as siape | preceptor as corem

    @Column(unique = true)
    private String username;

    private String password;

    private Boolean male;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private InternshipRole internshipRole;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private Boolean enabled;

    @ElementCollection
    @CollectionTable(name = "user_preferred_unities", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "unity_id")
    private List<UUID> preferredUnityIds = new ArrayList<>();

    public User(UserRegisterDTO data, String encryptedPassword, UserRole userRole) {
        this.username = data.username();
        this.name = data.name();
        this.email = data.email();
        this.password = encryptedPassword;
        this.userRole = userRole;
        this.registration = data.registration();
        this.internshipRole = data.internshipRole();
        this.male = data.male();
        this.enabled = true;
    }

    // Construtores de atualização para cada tipo de usuário
    public User(User existing, StudentUpdateDTO dto) {
        this.id = existing.id;
        this.username = dto.username() != null ? dto.username() : existing.username;
        this.name = dto.name() != null ? dto.name() : existing.name;
        this.email = dto.email() != null ? dto.email() : existing.email;
        this.registration = dto.registration() != null ? dto.registration() : existing.registration;
        this.internshipRole = dto.internshipRole() != null ? dto.internshipRole() : existing.internshipRole;
        this.enabled = dto.enabled() != null ? dto.enabled() : existing.enabled;
        this.male = dto.male() != null ? dto.male() : existing.male;
        this.password = existing.password;
        this.userRole = existing.userRole;
    }

    public User(User existing, SupervisorUpdateDTO dto) {
        this.id = existing.id;
        this.username = dto.username() != null ? dto.username() : existing.username;
        this.name = dto.name() != null ? dto.name() : existing.name;
        this.email = dto.email() != null ? dto.email() : existing.email;
        this.registration = dto.registration() != null ? dto.registration() : existing.registration;
        this.enabled = dto.enabled() != null ? dto.enabled() : existing.enabled;
        this.male = dto.male() != null ? dto.male() : existing.male;
        this.password = existing.password;
        this.userRole = existing.userRole;
        this.internshipRole = existing.internshipRole;
    }

    public User(User existing, PreceptorUpdateDTO dto) {
        this.id = existing.id;
        this.username = dto.username() != null ? dto.username() : existing.username;
        this.name = dto.name() != null ? dto.name() : existing.name;
        this.email = dto.email() != null ? dto.email() : existing.email;
        this.registration = dto.registration() != null ? dto.registration() : existing.registration;
        this.enabled = dto.enabled() != null ? dto.enabled() : existing.enabled;
        this.male = dto.male() != null ? dto.male() : existing.male;
        this.password = existing.password;
        this.userRole = existing.userRole;
        this.internshipRole = existing.internshipRole;
    }

    public User(User existing, AdminUpdateDTO dto, String encryptedPassword) {
        this.id = existing.id;
        this.username = dto.username() != null ? dto.username() : existing.username;
        this.name = dto.name() != null ? dto.name() : existing.name;
        this.email = dto.email() != null ? dto.email() : existing.email;
        this.password = (dto.password() != null && !dto.password().isEmpty()) ? encryptedPassword : existing.password;
        this.enabled = dto.enabled() != null ? dto.enabled() : existing.enabled;
        this.male = dto.male() != null ? dto.male() : existing.male;
        this.userRole = existing.userRole;
        this.registration = existing.registration;
        this.internshipRole = existing.internshipRole;
    }

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority(userRole.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    @NonNull
    public String getUsername() {
        return username;
    }


    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled != null ? enabled : false;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
}