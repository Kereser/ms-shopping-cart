package com.emazon.ms_shopping_cart.application.mapper;

import com.emazon.ms_shopping_cart.application.dto.ItemsReqDTO;
import com.emazon.ms_shopping_cart.application.dto.CartItemDTO;
import com.emazon.ms_shopping_cart.application.dto.out.CartPageDTO;
import com.emazon.ms_shopping_cart.application.dto.out.SaleDTO;
import com.emazon.ms_shopping_cart.application.dto.out.SaleItemDTO;
import com.emazon.ms_shopping_cart.domain.model.Cart;
import com.emazon.ms_shopping_cart.domain.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CartDTOMapper {

    Set<CartItem> cartItemsReqToCartItems(Set<CartItemDTO> setDTO);
    CartItemDTO cartItemToCartItemDTO(CartItem item);

    default ItemsReqDTO cartItemsToItemsReqDTO(Set<CartItem> items) {
        ItemsReqDTO itemsReqDTO = new ItemsReqDTO();

        Set<CartItemDTO> itemDTOList = items.stream()
                .map(this::cartItemToCartItemDTO)
                .collect(Collectors.toSet());

        itemsReqDTO.setItems(itemDTOList);
        return itemsReqDTO;
    }

    CartPageDTO cartToCartPage(Cart cart);

    SaleItemDTO cartItemToSaleItemDTO(CartItem cartItem);

    @Mapping(source = "cartItems", target = "saleArticles")
    SaleDTO cartToSaleDTO(Cart cart);
}
