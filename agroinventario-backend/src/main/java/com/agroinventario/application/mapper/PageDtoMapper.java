package com.agroinventario.application.mapper;

import com.agroinventario.application.dto.response.PageResponse;
import com.agroinventario.domain.model.PageResult;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.function.Function;

@Mapper(componentModel = "spring")
public interface PageDtoMapper {

    default <S, T> PageResponse<T> toPageResponse(PageResult<S> page, Function<S, T> mapper) {
        List<T> mapped = page.content().stream().map(mapper).toList();
        return new PageResponse<>(mapped, page.page(), page.size(), page.totalElements(), page.totalPages());
    }
}
