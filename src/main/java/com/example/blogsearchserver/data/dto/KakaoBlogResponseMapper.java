package com.example.blogsearchserver.data.dto;

import com.example.blogsearchserver.data.domain.KakaoBlogResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface KakaoBlogResponseMapper extends GenericMapper<BlogResponseDTO, KakaoBlogResponse> {
    KakaoBlogResponseMapper MAPPER = Mappers.getMapper(KakaoBlogResponseMapper.class);
}
