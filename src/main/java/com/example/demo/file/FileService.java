package com.example.demo.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FileService {

    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public Optional<FileDto> getFile(Integer id) {
        log.info("Looking for file by id {}", id);
        return fileRepository.getFileEntityById(id).map(FileMapper::mapToDto);
    }

    public List<FileDto> getFilesByUserName(String userName) {
        log.info("Looking for files by userName {}", userName);
        return fileRepository.findByUserName(userName).stream()
                .map(FileMapper::mapToDto)
                .toList();
    }
}
