package com.emazon.ms_shopping_cart.infra.input.rest;

import com.emazon.ms_shopping_cart.application.dto.ItemsReqDTO;
import com.emazon.ms_shopping_cart.application.handler.ICartHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final ICartHandler cartHandler;

    @PutMapping("/{userId}")
    public ResponseEntity<Void> handleAddOperation(@PathVariable Long userId, @RequestBody @Valid ItemsReqDTO dto) {
        cartHandler.handleAddOperation(userId, dto.getItems());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
