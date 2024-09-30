package com.emazon.ms_shopping_cart;

import com.emazon.ms_shopping_cart.application.dto.CartItemDTO;
import com.emazon.ms_shopping_cart.application.dto.ItemsReqDTO;
import com.emazon.ms_shopping_cart.application.dto.input.ArticlesPriceDTO;
import com.emazon.ms_shopping_cart.application.dto.out.ArticleResDTO;
import com.emazon.ms_shopping_cart.application.dto.out.BrandResDTO;
import com.emazon.ms_shopping_cart.application.dto.out.CategoryArticleResDTO;
import com.emazon.ms_shopping_cart.domain.model.Cart;
import com.emazon.ms_shopping_cart.domain.model.CartItem;
import com.emazon.ms_shopping_cart.infra.security.model.CustomUserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class TestCreationUtils {
    private TestCreationUtils() {
    }

    public static final String BRAND_NAME = "BrandName";
    public static final String CATEGORY_NAME = "CategoryName";
    public static final String ARTICLE_NAME = "ArticleName";
    public static final String ARTICLE_DESCRIPTION = "ArticleDescription";

    private static Long categoryIdCounter = 1L;
    private static Long cartIdCounter = 1L;
    private static Long brandIdCounter = 1L;
    private static Long articleIdCounter = 1L;
    private static Long defaultCounter = 1L;

    private static Integer prefixCounter = 1;
    private static Integer categoryPrefixCounter = 1;
    private static Integer articlePrefixCounter = 1;

    private static final BigDecimal BASIC_PRICE = BigDecimal.valueOf(100.00);
    private static final Long BASIC_QUANTITY = 1L;
    private static Long baseQuantity = 10L;

    public static CategoryArticleResDTO createCategory() {
        return CategoryArticleResDTO.builder()
                .id(categoryIdCounter++)
                .name(CATEGORY_NAME + categoryPrefixCounter++)
                .build();
    }

    public static BrandResDTO createBrand() {
        return BrandResDTO.builder()
                .id(brandIdCounter++)
                .name(BRAND_NAME + prefixCounter++)
                .description(BRAND_NAME + prefixCounter++)
                .build();
    }

    public static ArticleResDTO createArticleRes() {
        return createArticleRes(null);
    }

    public static ArticleResDTO createArticleRes(Long articleId) {
        return ArticleResDTO.builder()
                .id(articleId == null ? articleIdCounter++ : articleId)
                .name(ARTICLE_NAME + articlePrefixCounter)
                .description(ARTICLE_DESCRIPTION + articlePrefixCounter)
                .price(BASIC_PRICE)
                .quantity(baseQuantity++)
                .categories(Set.of(
                        createCategory(),
                        createCategory()
                ))
                .brand(createBrand())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static ArticlesPriceDTO createArticlePriceDTO(Long id) {
        return ArticlesPriceDTO.builder()
                .id(id != null ? id : articleIdCounter++)
                .price(BigDecimal.TEN)
                .quantity(BASIC_QUANTITY)
                .build();
    }

    public static Set<ArticlesPriceDTO> createArticlePriceDTOFromCartItems(Set<CartItemDTO> cartItems) {
        Set<ArticlesPriceDTO> res = new HashSet<>();

        cartItems.forEach(ci -> {
            ArticlesPriceDTO dto = new ArticlesPriceDTO();

            dto.setId(ci.getArticleId());
            dto.setPrice(BigDecimal.TEN);
            dto.setQuantity(ci.getQuantity());

            res.add(dto);
        });

        return res;
    }

    public static CartItem createCartItem() {
        return new CartItem(cartIdCounter, defaultCounter++, BASIC_QUANTITY);
    }

    public static Cart createCart() {
        return new Cart(cartIdCounter++, defaultCounter++, new HashSet<>(Set.of(createCartItem())), BigDecimal.valueOf(500), LocalDateTime.now(), LocalDateTime.now());
    }

    public static CartItemDTO getCartItemDTO() {
        return CartItemDTO.builder().articleId(cartIdCounter++).quantity(ConsUtils.LONG_1).build();
    }

    public static ItemsReqDTO getItemsReqDTO() {
        return ItemsReqDTO.builder()
                .items(new HashSet<>(Set.of(getCartItemDTO())))
                .build();
    }

    public static CustomUserDetails buildUserDetails() {
        return new CustomUserDetails(ConsUtils.USERNAME,
                ConsUtils.PASSWORD,
                Set.of(new SimpleGrantedAuthority(ConsUtils.ROLE.concat(ConsUtils.CLIENT))),
                defaultCounter);
    }
}
