package de.trzpiot.postgresql_job_queue_example.controller;

import de.trzpiot.postgresql_job_queue_example.controller.model.FileModel;
import de.trzpiot.postgresql_job_queue_example.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class FileController {

    FileService service;

    @PostMapping("/upload-jpeg")
    public ResponseEntity<Void> uploadJpeg(final MultipartFile file) {
        service.saveFile(file);
        return ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<FileModel>> getMetadataOfAllFiles() {
        return ok(service.getMetadataOfAllFiles());
    }

    @GetMapping(value = "/download-pdf/{id}", produces = APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadPdf(@PathVariable final Long id) {
        return ok(service.getPdf(id));
    }
}
