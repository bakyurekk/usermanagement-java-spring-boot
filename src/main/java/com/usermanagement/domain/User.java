package com.usermanagement.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(name="user_id")
    private String userId;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Column(name="username")
    private String username;

    @Column(name="password")
    private String password;

    @Column(name="email")
    private String email;

    @Column(name="profile_image_url")
    private String profileImageUrl;

    @Column(name="last_login_date")
    private Date lastLoginDate;

    @Column(name="last_login_date_display")
    private Date lastLoginDateDisplay;

    @Column(name="join_date")
    private Date joinDate;

    @Column(name="role")
    private String role;

    @Column(name="authorities")
    private String[] authorities;

    @Column(name="is_active")
    private boolean isActive;

    @Column(name="is_not_locked")
    private boolean isNotLocked;
}
