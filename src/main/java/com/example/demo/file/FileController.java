package com.example.demo.file;

import com.example.demo.messages.MessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Controller
public class FileController {

    private final FileService fileService;
    private final MessageProducer messageProducer;

    public FileController(FileService fileService, MessageProducer messageProducer) {
        this.fileService = fileService;
        this.messageProducer = messageProducer;
    }

    @GetMapping("/file/{id}")
    public ResponseEntity<FileDto> getFile(@PathVariable int id) {
        Optional<FileDto> file = fileService.getFile(id);

        return file.map(fileDto -> new ResponseEntity<>(fileDto, OK))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }

    @GetMapping("/files/{userName}")
    public ResponseEntity<List<FileDto>> getFilesByUserName(@PathVariable String userName) {
        List<FileDto> files = fileService.getFilesByUserName(userName);

        return files.isEmpty()
                ? new ResponseEntity<>(NOT_FOUND)
                : new ResponseEntity<>(files, OK);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        var test = "Test";
        messageProducer.sendMessage(test);
        return new ResponseEntity<>(test, OK);
    }

}
