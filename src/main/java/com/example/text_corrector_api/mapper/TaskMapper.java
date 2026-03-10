package com.example.text_corrector_api.mapper;

import com.example.text_corrector_api.dao.entity.TaskEntity;
import com.example.text_corrector_api.dto.TaskCreateDto;
import com.example.text_corrector_api.dto.TaskProcessingContext;
import com.example.text_corrector_api.dto.TaskResponseDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TaskMapper {
    TaskResponseDto toTaskResponseDto(TaskEntity taskEntity);
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "originalText", source = "text")
    @Mapping(target = "language", source = "language")
    TaskEntity toTaskEntity(TaskCreateDto taskCreateDto);

    @Mapping(target = "text", source = "originalText")
    TaskProcessingContext toTaskProcessingContext(TaskEntity taskEntity);
}
