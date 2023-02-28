package de.trzpiot.postgresql_job_queue_example.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static lombok.AccessLevel.PRIVATE;

/**
 * Scheduler service that includes cron jobs for processing files.
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class FileConvertScheduler {

    FileConvertService service;

    /**
     * Cronjob for processing files.
     * Runs to second 0 at every minute.
     *
     * @see FileConvertService#convertPendingFiles()
     */
    @Scheduled(cron = "0 * * * * *")
    public void convertPendingFiles() {
        service.convertPendingFiles();
    }
}
