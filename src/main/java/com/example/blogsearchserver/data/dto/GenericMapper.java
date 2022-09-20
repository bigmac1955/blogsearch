package com.example.blogsearchserver.data.dto;

import java.util.List;

public interface GenericMapper<D, E>{

    D toDto(final E entity);

    List<D> toDto(List<E> e);
}
