package com.agit.peerflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bootcamp_classes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BootcampClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 반 이름 (예: 2기 백엔드 트랙)
    @Column(nullable = false)
    private String name;

    // 강사 (1명)
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    // 학생들 (N:M)
    @ManyToMany(mappedBy = "enrolledClasses")
    private List<User> students = new ArrayList<>();
}
