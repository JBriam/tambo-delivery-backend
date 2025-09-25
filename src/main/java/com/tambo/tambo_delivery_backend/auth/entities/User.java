package com.tambo.tambo_delivery_backend.auth.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tambo.tambo_delivery_backend.entities.Address;
import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Table(name = "AUTH_USER_DETAILS")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private UUID id;

    @Builder.Default
    private boolean enabled = false;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    private String password;

    private String provider;

    private String verificationCode;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private Date resetTokenExpiry;

    @Column(name = "last_password_reset_request")
    private Date lastPasswordResetRequest;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdOn;

    @UpdateTimestamp
    @Column(nullable = false)
    private Date updatedOn;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "AUTH_USER_AUTHORITY", joinColumns = @JoinColumn(referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(referencedColumnName = "id"))
    private List<Authority> authorities;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Address> addressList;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

}
