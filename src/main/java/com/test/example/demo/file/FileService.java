package com.test.example.demo.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    public Optional<FileDto> getFile(Integer id) {
        log.info("Looking for file by id {}", id);
        Optional<File> fileEntity = fileRepository.getFileEntityById(id);
        return fileEntity.map(FileMapper::mapToDto);
    }

    public List<FileDto> getFilesByUserName(String userName) {
        List<File> filesFromDB = fileRepository.findByUserName(userName);
        return filesFromDB.stream()
                .map(FileMapper::mapToDto)
                .toList();
    }


}
