package de.trzpiot.postgresql_job_queue_example.controller.model;

/**
 * Reflects the status of the job in the queue.
 */
public enum Status {
    /**
     * The processing of the job is still pending.
     */
    PENDING,

    /**
     * Processing of the job has been completed.
     */
    DONE
}
