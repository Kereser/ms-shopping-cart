package com.emazon.ms_shopping_cart.infra.input.rest;

import com.emazon.ms_shopping_cart.ConsUtils;
import com.emazon.ms_shopping_cart.application.dto.ItemsReqDTO;
import com.emazon.ms_shopping_cart.application.dto.out.ArticleResDTO;
import com.emazon.ms_shopping_cart.application.dto.handlers.PageDTO;
import com.emazon.ms_shopping_cart.application.handler.ICartHandler;
import com.emazon.ms_shopping_cart.domain.model.SortOrder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ConsUtils.BASIC_URL)
@RequiredArgsConstructor
public class CartController {

    private final ICartHandler cartHandler;

    @PutMapping
    public ResponseEntity<Void> handleAddOperation(@RequestBody @Valid ItemsReqDTO dto) {
        cartHandler.handleAddOperation(dto.getItems());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping(ConsUtils.DELETE_ITEM_ROUTE)
    public ResponseEntity<Void> deleteItemFromCart(@PathVariable Long cartId, @PathVariable Long articleId) {
        cartHandler.handleDeletionFromCart(cartId, articleId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ConsUtils.GET_ALL_ITEMS_FOR_USER)
    public ResponseEntity<PageDTO<ArticleResDTO>> getAllCartItems(
            @RequestParam(defaultValue = ConsUtils.ASC) SortOrder direction,
            @RequestParam(defaultValue = ConsUtils.INTEGER_STR_20) Integer pageSize,
            @RequestParam(defaultValue = ConsUtils.INTEGER_STR_0) Integer page,
            @RequestParam(defaultValue = ConsUtils.EMPTY) String categoryName,
            @RequestParam(defaultValue = ConsUtils.EMPTY) String brandName) {
        return ResponseEntity.ok().body(cartHandler.getAllCartItems(direction.name(), pageSize, page, categoryName, brandName));
    }

    @PostMapping(ConsUtils.WITH_CHECKOUT_URL)
    public ResponseEntity<Void> buyCart() {
        cartHandler.buyCart();
        return ResponseEntity.ok().build();
    }
}
