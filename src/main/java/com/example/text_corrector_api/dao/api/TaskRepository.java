package com.example.text_corrector_api.dao.api;

import com.example.text_corrector_api.dao.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {

    @Query(value = """
            SELECT id, original_text, corrected_text, status, language, error_message, created_at, updated_at
            FROM app.tasks
            WHERE status = 'NEW'
            ORDER BY created_at ASC
            LIMIT :limit
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<TaskEntity> findNewTasksForProcessing(@Param("limit") int limit);
}
