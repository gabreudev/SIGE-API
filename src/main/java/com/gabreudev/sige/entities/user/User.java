package com.gabreudev.sige.entities.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;


@Entity
@NoArgsConstructor
@Table(name = "users")
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String registration; // student as matricula and supervisor as siape

    private String username;

    private String password;

    private String email;

    private String coren;

    @Enumerated(EnumType.STRING)
    private InternshipRole internshipRole;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private Boolean enabled;



    public User(String username, String email, String password, UserRole userRole, Boolean enabled, String coren, String registration) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
        this.coren = coren;
        this.registration = registration;
        this.enabled = enabled != null ? enabled : true;
    }

    public User(UserRegisterDTO data, String passwordBcripted) {
        this.email = data.email();
        this.password = passwordBcripted;
        this.userRole = data.userRole();
        this.internshipRole = data.internshipRole();
        this.username = data.username();
        this.registration = data.registration();
        this.enabled = true;
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