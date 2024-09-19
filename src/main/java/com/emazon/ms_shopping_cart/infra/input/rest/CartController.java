package com.emazon.ms_shopping_cart.infra.input.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
public class CartController {
    /* create ->
        get ArticleIds with quantity;
        get userId;
    * */

    /* Update ->
        get articleIds with quantity;
        param -> userId;
    * */
    @PutMapping
    public ResponseEntity<String> updateCart() {
        return ResponseEntity.ok("Cart updated");
    }

    /* Delete ->
       param cartId;
    * */

}
