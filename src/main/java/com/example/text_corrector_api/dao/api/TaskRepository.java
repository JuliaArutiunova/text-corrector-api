package com.example.text_corrector_api.dao.api;

import com.example.text_corrector_api.dao.entity.TaskEntity;
import com.example.text_corrector_api.enums.TaskStatus;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {

    List<TaskEntity> findAllByStatusOrderByCreatedAtAsc(TaskStatus status, Limit limit);
}
