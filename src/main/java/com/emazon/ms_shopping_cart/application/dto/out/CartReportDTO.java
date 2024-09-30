package com.emazon.ms_shopping_cart.application.dto.out;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartReportDTO {
    private Long cartId;
    private Long userId;
    private BigDecimal totalPrice;
    private Set<ArticleResDTO> articles;
    private LocalDateTime cartLastUpdatedAt;
}
