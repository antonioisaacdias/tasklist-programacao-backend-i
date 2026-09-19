package com.antoniodias.tasklist.task.entity;

import com.antoniodias.tasklist.tag.entity.Tag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Entity
@Table(
        name = "task_tag",
        uniqueConstraints = @UniqueConstraint(columnNames = {"task_id", "tag_id"})
)
public class TaskTag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;
}
