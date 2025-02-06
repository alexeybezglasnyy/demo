package com.test.example.demo.file;

import com.test.example.demo.messages.MessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Controller
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private MessageProducer messageProducer;

    @GetMapping("/file/{id}")
    public ResponseEntity<FileDto> getFile(@PathVariable int id) {
        Optional<FileDto> file = fileService.getFile(id);
        messageProducer.sendMessage(format("Looking for file by id %d", id));
        return file.map(fileDto -> new ResponseEntity<>(fileDto, OK))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }

    @GetMapping("/files/{userName}")
    public ResponseEntity<List<FileDto>> getFilesByUserName(@PathVariable String userName) {
        List<FileDto> files = fileService.getFilesByUserName(userName);
        messageProducer.sendMessage(format("Looking for file by userName %s", userName));
        return files.isEmpty()
                ? new ResponseEntity<>(NOT_FOUND)
                : new ResponseEntity<>(files, OK);
    }

}
