package com.agit.peerflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String nickname;

    // STUDENT / TEACHER / ADMIN
    @Column(nullable = false)
    private String role;

    // ACTIVE / PENDING / INACTIVE
    @Column(nullable = false)
    private String status;

    // 학생 → 수강중인 BootcampClass
    @ManyToMany
    @JoinTable(
            name = "user_bootcamp_classes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "bootcamp_class_id")
    )
    private List<BootcampClass> enrolledClasses = new ArrayList<>();

    // 강사 → 담당 BootcampClass
    @OneToMany(mappedBy = "teacher")
    private List<BootcampClass> teachingClasses = new ArrayList<>();
}
