package com.emazon.ms_shopping_cart.infra.input.rest;

import com.emazon.ms_shopping_cart.ConsUtils;
import com.emazon.ms_shopping_cart.application.dto.ItemsReqDTO;
import com.emazon.ms_shopping_cart.application.handler.ICartHandler;
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
}
