package de.trzpiot.postgresql_job_queue_example.service;

import de.trzpiot.postgresql_job_queue_example.controller.model.Status;
import de.trzpiot.postgresql_job_queue_example.domain.FileEntity;
import de.trzpiot.postgresql_job_queue_example.exception.FileAlreadyProcessedException;
import de.trzpiot.postgresql_job_queue_example.repository.FileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.io.File.createTempFile;
import static java.text.MessageFormat.format;
import static lombok.AccessLevel.PRIVATE;

/**
 * Service for converting files, for example JPEG to PDF.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class FileConvertService {

    public static final int EXIT_CODE_SUCCESS = 0;
    FileRepository repository;
    TransactionTemplate transactionTemplate;

    /**
     * Converts all files that have the {@link Status#PENDING}.
     * This means that the uploaded JPEG will be converted to PDF using {@code img2pdf}.
     * This happens asynchronously in individual transactions (for each file a single transaction is opened), so that in case of errors in the processing of one file does not affect the processing of other files.
     */
    @Transactional
    public void convertPendingFiles() {
        repository.getAllPending().forEach(file -> transactionTemplate.executeWithoutResult(status -> process(file)));
    }

    @Async
    private void process(final FileEntity file) {
        log.info("Converting for file '{}'", file.getId());
        file.setData(convert(file));
        advanceToNextStatus(file);
        log.info("Finished converting for file '{}'", file.getId());
    }

    private byte[] convert(FileEntity file) {
        try {
            return convert(file.getData());
        } catch (final IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] convert(final byte[] jpegData) throws IOException, InterruptedException {
        final var tmpJpegFile = createTmpJpegFile(jpegData);
        final var tmpPdfFile = createTempFile("tmp", ".pdf");
        convertViaImg2Pdf(tmpJpegFile, tmpPdfFile);
        final var pdfData = getPdfData(tmpPdfFile);
        tmpJpegFile.delete();
        tmpPdfFile.delete();
        return pdfData;
    }

    private File createTmpJpegFile(byte[] jpegData) throws IOException {
        final var tmpJpegFile = createTempFile("tmp", ".jpg");
        final var fileOutputStream = new FileOutputStream(tmpJpegFile);
        fileOutputStream.write(jpegData);
        fileOutputStream.close();
        return tmpJpegFile;
    }

    private void convertViaImg2Pdf(final File tmpJpegFile, final File tmpPdfFile) throws IOException, InterruptedException {
        final var processBuilder = new ProcessBuilder("img2pdf", tmpJpegFile.getAbsolutePath(), "-o", tmpPdfFile.getAbsolutePath());
        final var process = processBuilder.start();
        final var exitCode = process.waitFor();

        if (exitCode != EXIT_CODE_SUCCESS) {
            throw new IOException(format("img2pdf returned exit code {0}", exitCode));
        }
    }

    private byte[] getPdfData(File tmpPdfFile) throws IOException {
        final byte[] pdfData;
        final var fileInputStream = new FileInputStream(tmpPdfFile);
        pdfData = fileInputStream.readAllBytes();
        fileInputStream.close();
        return pdfData;
    }

    private void advanceToNextStatus(FileEntity file) {
        try {
            file.setNextStatus();
        } catch (final FileAlreadyProcessedException e) {
            throw new RuntimeException(e);
        }
    }
}
