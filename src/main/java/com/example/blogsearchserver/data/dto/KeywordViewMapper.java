package com.example.blogsearchserver.data.dto;

import com.example.blogsearchserver.data.entity.KeywordView;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface KeywordViewMapper extends GenericMapper<KeywordViewDTO, KeywordView> {
        KeywordViewMapper MAPPER = Mappers.getMapper(KeywordViewMapper.class);
}
