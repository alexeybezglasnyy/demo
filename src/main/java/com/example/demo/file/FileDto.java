package com.example.demo.file;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileDto {

    private String token;
    private String name;
    private String status;
    private Long size;
    private String lastUpdatedTime;
}
