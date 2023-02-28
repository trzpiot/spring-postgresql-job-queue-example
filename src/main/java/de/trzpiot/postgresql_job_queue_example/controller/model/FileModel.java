package de.trzpiot.postgresql_job_queue_example.controller.model;

import java.time.LocalDateTime;

/**
 * DTO that represents a saved file in the database, but without the contents of the file (only metadata).
 */
public record FileModel(
        long id,
        String name,
        LocalDateTime creation,
        Status status
) {
}
