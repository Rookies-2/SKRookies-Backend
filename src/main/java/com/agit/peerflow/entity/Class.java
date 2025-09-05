package com.agit.peerflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "classes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    // 학생 목록
    @ManyToMany(mappedBy = "enrolledClasses")
    private List<User> students;

    // 강사
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;
}
