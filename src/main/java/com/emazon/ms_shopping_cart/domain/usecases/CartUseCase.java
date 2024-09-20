package com.emazon.ms_shopping_cart.domain.usecases;

import com.emazon.ms_shopping_cart.ConsUtils;
import com.emazon.ms_shopping_cart.application.mapper.CartDTOMapper;
import com.emazon.ms_shopping_cart.domain.api.ICartServicePort;
import com.emazon.ms_shopping_cart.domain.model.Cart;
import com.emazon.ms_shopping_cart.domain.model.CartItem;
import com.emazon.ms_shopping_cart.domain.spi.ICartPersistencePort;
import com.emazon.ms_shopping_cart.domain.spi.StockFeignPort;
import com.emazon.ms_shopping_cart.infra.exception.NoDataFoundException;
import com.emazon.ms_shopping_cart.infra.out.jpa.entity.CartEntity;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CartUseCase implements ICartServicePort {

    private final ICartPersistencePort cartPersistencePort;
    private final StockFeignPort stockFeignPort;
    private final CartDTOMapper mapper;

    public CartUseCase(ICartPersistencePort cartPersistencePort, StockFeignPort stockFeignPort, CartDTOMapper mapper) {
        this.cartPersistencePort = cartPersistencePort;
        this.stockFeignPort = stockFeignPort;
        this.mapper = mapper;
    }

    @Override
    public void handleAddOperation(Cart cartReq) {
        assignUserIdFromToken(cartReq);

        Optional<Cart> optCart = findCartByUserIdOpt(cartReq);

        if (optCart.isEmpty()) {
            handleNewCart(cartReq);
            return;
        }

        handleUpdateCart(cartReq, optCart.get());
    }

    public Optional<Cart> findCartByUserIdOpt(Cart cartReq) {
        return cartPersistencePort.findByUserId(cartReq.getUserId());
    }

    @Override
    public void deleteArticleFromCart(Long cartId, Long articleId) {
        Cart cart = findCartByUserId();
        CartItem cartItemToRemove = findCartItemToDelete(cart.getCartItems(), articleId);

        removeCartItemFromCart(cart, cartItemToRemove);

        save(cart);
    }

    @Override
    public void save(Cart cart) {
        cartPersistencePort.save(cart);
    }

    private void assignUserIdFromToken(Cart cartReq) {
        cartReq.setUserId(getUserIdFromToken());
    }

    private Long getUserIdFromToken() {
        return cartPersistencePort.getSecurityPrincipal().getUserId();
    }

    private void handleNewCart(Cart cartReq) {
        stockFeignPort.handleAdditionToCart(mapper.cartItemsToItemsReqDTO(cartReq.getCartItems()));

        cartReq.setCreatedAt(LocalDateTime.now());
        save(cartReq);
    }

    private void handleUpdateCart(Cart cartReq, Cart dbCart) {
        Set<CartItem> tentativeItems = getAllNewPossibleItemsOnCart(cartReq.getCartItems(), dbCart.getCartItems());

        stockFeignPort.handleAdditionToCart(mapper.cartItemsToItemsReqDTO(tentativeItems));

        dbCart.addItems(tentativeItems);
        save(dbCart);
    }

    private Set<CartItem> getAllNewPossibleItemsOnCart(Set<CartItem> toAdd, Set<CartItem> actual) {
        Map<Long, Long> toAddMap = mapItemsToQuantity(toAdd);
        Map<Long, CartItem> actualItemsMap = mapItemsToCartItems(actual);

        updateCartItemsWithNewQuantities(toAddMap, actualItemsMap);

        return new HashSet<>(actualItemsMap.values());
    }

    private Map<Long, Long> mapItemsToQuantity(Set<CartItem> items) {
        return items.stream().collect(Collectors.toMap(CartItem::getArticleId, CartItem::getQuantity));
    }

    private Map<Long, CartItem> mapItemsToCartItems(Set<CartItem> items) {
        return items.stream().collect(Collectors.toMap(CartItem::getArticleId, item -> item));
    }

    private void updateCartItemsWithNewQuantities(Map<Long, Long> toAddMap, Map<Long, CartItem> actualItemsMap) {
        toAddMap.forEach((id, quantity) -> {
            CartItem ci = actualItemsMap.get(id);
            if (ci != null) {
                ci.setQuantity(quantity + ci.getQuantity());
            } else {
                actualItemsMap.put(id, createNewCartItem(id, quantity));
            }
        });
    }

    private CartItem createNewCartItem(Long articleId, Long quantity) {
        CartItem newCartItem = new CartItem();
        newCartItem.setArticleId(articleId);
        newCartItem.setQuantity(quantity);
        return newCartItem;
    }

    private Cart findCartByUserId() {
        Optional<Cart> optCart = cartPersistencePort.findByUserId(getUserIdFromToken());

        if (optCart.isEmpty()) throw new NoDataFoundException(Cart.class.getSimpleName(), CartEntity.Fields.id);

        return optCart.get();
    }

    private CartItem findCartItemToDelete(Set<CartItem> cartItems, Long articleId) {
        return cartItems.stream()
                .filter(ci -> ci.getArticleId() == articleId.longValue())
                .findFirst()
                .orElseThrow(() -> new NoDataFoundException(ConsUtils.ARTICLE_ENTITY, CartEntity.Fields.id));
    }

    private void removeCartItemFromCart(Cart cart, CartItem cartItemToRemove) {
        cart.deleteItems(Set.of(cartItemToRemove));
    }
}
