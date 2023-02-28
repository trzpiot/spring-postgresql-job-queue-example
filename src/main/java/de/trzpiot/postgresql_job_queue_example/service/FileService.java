package de.trzpiot.postgresql_job_queue_example.service;

import de.trzpiot.postgresql_job_queue_example.controller.model.FileModel;
import de.trzpiot.postgresql_job_queue_example.controller.model.Status;
import de.trzpiot.postgresql_job_queue_example.domain.FileEntity;
import de.trzpiot.postgresql_job_queue_example.repository.FileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static de.trzpiot.postgresql_job_queue_example.controller.model.Status.DONE;
import static java.text.MessageFormat.format;
import static lombok.AccessLevel.PRIVATE;

/**
 * Service that provides methods for processing files in the context of the database.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class FileService {

    FileRepository repository;

    /**
     * Saves the contents of the file & metadata to the database.
     *
     * @param file the file to be processed
     */
    public void saveFile(final MultipartFile file) {
        repository.save(new FileEntity(file.getOriginalFilename(), getData(file)));
    }

    private byte[] getData(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the contents of a saved file in the database.
     * For this, the processing of the file must already be completed (status is set to {@link Status#DONE}).
     *
     * @param id the ID of the file
     * @return the content of the file
     */
    public byte[] getPdf(final Long id) {
        final var file = findById(id);
        validateThatPdfIsReady(file);
        return file.getData();
    }

    private void validateThatPdfIsReady(FileEntity file) {
        if (file.getStatus() != DONE) {
            throw new IllegalStateException(format("File {0} is not ready yet", file.getId()));
        }
    }

    private FileEntity findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(format("No file with ID {0} found", id)));
    }

    /**
     * Returns the metadata of all saved files.
     *
     * @return the metadata of all saved files
     */
    public List<FileModel> getMetadataOfAllFiles() {
        return repository.findAll()
                .stream()
                .map(file -> new FileModel(file.getId(), file.getName(), file.getCreation(), file.getStatus()))
                .toList();
    }
}
