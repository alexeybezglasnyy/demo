package com.example.demo.file;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Table(name = "files")
public class File {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    private String token;

    private String name;

    @Enumerated(STRING)
    private FileStatus state;

    private Long size;

    @Column(name = "last_updated_time")
    private LocalDateTime lastUpdatedTime;

    @Column(name = "user_name")
    private String userName;
}
