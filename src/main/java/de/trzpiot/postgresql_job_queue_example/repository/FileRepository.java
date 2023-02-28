package de.trzpiot.postgresql_job_queue_example.repository;

import de.trzpiot.postgresql_job_queue_example.controller.model.Status;
import de.trzpiot.postgresql_job_queue_example.domain.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    /**
     * Returns all saved files that have the {@link Status#PENDING}.
     * Sorted in ascending order by creation date (the oldest first).
     * A maximum of 2 files are retrieved.
     * {@code FOR UPDATE} of PostgreSQL is used to block the entities within the transaction.
     * To ensure that only non-blocked entities are retrieved {@code SKIP LOCKED} is used.
     *
     * @return the result of the query
     */
    @Query(value = "SELECT * FROM file WHERE status = 'PENDING' ORDER BY creation ASC FOR UPDATE SKIP LOCKED LIMIT 2", nativeQuery = true)
    List<FileEntity> getAllPending();
}
