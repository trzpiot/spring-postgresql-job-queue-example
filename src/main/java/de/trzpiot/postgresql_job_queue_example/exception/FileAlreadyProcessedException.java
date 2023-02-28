package de.trzpiot.postgresql_job_queue_example.exception;

import de.trzpiot.postgresql_job_queue_example.controller.model.Status;
import de.trzpiot.postgresql_job_queue_example.domain.FileEntity;

/**
 * Can be thrown if the {@link FileEntity} is already processed, i.e. it has the {@link Status#DONE}.
 */
public class FileAlreadyProcessedException extends Exception {
}
