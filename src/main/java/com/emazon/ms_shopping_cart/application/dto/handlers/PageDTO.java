package com.emazon.ms_shopping_cart.application.dto.handlers;

import com.emazon.ms_shopping_cart.application.dto.out.CartPageDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PageDTO<T> {
    private Integer totalElements;
    private Integer totalPages;
    private Pageable pageable;
    private Integer numberOfElements;
    private Integer currentPage;
    private Integer size;
    private Boolean first;
    private Boolean last;
    private CartPageDTO cart;
    private List<T> content;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pageable {
        private Integer pageNumber;
        private Integer pageSize;
        private Integer offset;
    }
}
