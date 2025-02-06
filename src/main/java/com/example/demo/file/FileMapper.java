package com.example.demo.file;

import static com.example.demo.utils.Utils.formatDate;

public class FileMapper {


    public static FileDto mapToDto(File entity) {
        return FileDto.builder()
                .token(entity.getToken())
                .name(entity.getName())
                .size(entity.getSize())
                .status(entity.getState().name())
                .lastUpdatedTime(formatDate(entity.getLastUpdatedTime()))
                .build();
    }
}

