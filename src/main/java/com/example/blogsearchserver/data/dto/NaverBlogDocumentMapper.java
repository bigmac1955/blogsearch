package com.example.blogsearchserver.data.dto;

import com.example.blogsearchserver.data.domain.NaverBlogDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NaverBlogDocumentMapper extends GenericMapper<BlogResponseDocumentDTO, NaverBlogDocument> {

    NaverBlogDocumentMapper MAPPER = Mappers.getMapper(NaverBlogDocumentMapper.class);

    @Override
    @Mapping(source = "description", target = "contents")
    @Mapping(source = "bloggername", target = "blogname")
    @Mapping(source = "bloggerlink", target = "url")
    @Mapping(source = "postdate", target = "datetime")
    BlogResponseDocumentDTO toDto(NaverBlogDocument blogDocument);
}
