package com.example.demo.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Integer> {
    Optional<File> getFileEntityById(int id);
    List<File> findByUserName(String userName);
}
