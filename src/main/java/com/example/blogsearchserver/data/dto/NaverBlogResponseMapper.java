package com.example.blogsearchserver.data.dto;

import com.example.blogsearchserver.data.domain.NaverBlogResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NaverBlogResponseMapper extends GenericMapper<BlogResponseDTO, NaverBlogResponse> {

    NaverBlogResponseMapper MAPPER = Mappers.getMapper(NaverBlogResponseMapper.class);

    @Override
    @Mapping(source = "total", target = "totalElements")
    @Mapping(source = "start", target = "pageNumber")
    @Mapping(source = "display", target = "pageSize")
    @Mapping(source = "items", target = "documents")
    BlogResponseDTO toDto(NaverBlogResponse blogResponse);
}
