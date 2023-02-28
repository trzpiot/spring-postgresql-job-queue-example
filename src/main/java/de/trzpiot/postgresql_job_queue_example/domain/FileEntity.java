package de.trzpiot.postgresql_job_queue_example.domain;

import de.trzpiot.postgresql_job_queue_example.controller.model.Status;
import de.trzpiot.postgresql_job_queue_example.exception.FileAlreadyProcessedException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static de.trzpiot.postgresql_job_queue_example.controller.model.Status.DONE;
import static de.trzpiot.postgresql_job_queue_example.controller.model.Status.PENDING;
import static jakarta.persistence.EnumType.STRING;
import static java.time.LocalDateTime.now;
import static lombok.AccessLevel.PRIVATE;

@Getter
@Entity(name = "file")
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public class FileEntity {

    @Id
    @Setter
    @GeneratedValue
    Long id;

    @Enumerated(STRING)
    Status status;

    @Lob
    @Setter
    byte[] data;

    String name;
    LocalDateTime creation;

    public FileEntity(final String name, final byte[] data) {
        this.status = PENDING;
        this.data = data;
        this.name = name;
        this.creation = now();
    }

    /**
     * Moves to the next possible state in the state machine.
     * If an impossible state is reached, a {@link FileAlreadyProcessedException} is thrown.
     *
     * @throws FileAlreadyProcessedException if an impossible state is reached, e.g. status is already {@link Status#DONE}
     */
    public void setNextStatus() throws FileAlreadyProcessedException {
        switch (status) {
            case PENDING ->
                    this.status = DONE;
            case DONE ->
                    throw new FileAlreadyProcessedException();
        }
    }
}
