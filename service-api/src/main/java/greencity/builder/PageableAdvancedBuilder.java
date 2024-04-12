package greencity.builder;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import org.springframework.data.domain.Page;

import java.util.List;

public class PageableAdvancedBuilder {

    public static <T, S> PageableAdvancedDto<T> getPageableAdvancedDto(
        List<T> elements, Page<S> page) {
        var pageable = page.getPageable();

        return new PageableAdvancedDto<>(
            elements,
            page.getTotalElements(),
            pageable.isPaged() ? pageable.getPageNumber() : 0,
            page.getTotalPages(),
            page.getNumber(),
            page.hasPrevious(),
            page.hasNext(),
            page.isFirst(),
            page.isLast());
    }

    public static <T, S> PageableDto<T> getPageableDto(List<T> elements, Page<S> page) {
        var pageable = page.getPageable();

        return new PageableDto<>(elements,
                page.getTotalElements(),
                pageable.isPaged() ? pageable.getPageNumber() : 0,
                page.getTotalPages());
    }
}
