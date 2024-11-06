package com.emazon.ms_shopping_cart.domain.usecases;

import com.emazon.ms_shopping_cart.ConsUtils;
import com.emazon.ms_shopping_cart.application.dto.ItemsReqDTO;
import com.emazon.ms_shopping_cart.application.dto.handlers.PageDTO;
import com.emazon.ms_shopping_cart.application.dto.input.ArticlesPriceDTO;
import com.emazon.ms_shopping_cart.application.dto.out.*;
import com.emazon.ms_shopping_cart.application.mapper.CartDTOMapper;
import com.emazon.ms_shopping_cart.application.utils.ParsingUtils;
import com.emazon.ms_shopping_cart.domain.api.ICartServicePort;
import com.emazon.ms_shopping_cart.domain.model.Cart;
import com.emazon.ms_shopping_cart.domain.model.CartItem;
import com.emazon.ms_shopping_cart.domain.spi.ICartPersistencePort;
import com.emazon.ms_shopping_cart.domain.spi.ReportFeignPort;
import com.emazon.ms_shopping_cart.domain.spi.StockFeignPort;
import com.emazon.ms_shopping_cart.domain.spi.TransactionsFeignPort;
import com.emazon.ms_shopping_cart.infra.exception.NoDataFoundException;
import com.emazon.ms_shopping_cart.infra.exception.PurchaseFailedException;
import com.emazon.ms_shopping_cart.infra.out.jpa.entity.CartEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CartUseCase implements ICartServicePort {

    private final ICartPersistencePort cartPersistencePort;
    private final StockFeignPort stockFeignPort;
    private final TransactionsFeignPort transactionsFeignPort;
    private final ReportFeignPort reportFeignPort;
    private final CartDTOMapper mapper;

    public CartUseCase(ICartPersistencePort cartPersistencePort, StockFeignPort stockFeignPort, TransactionsFeignPort transactionsFeignPort, ReportFeignPort reportFeignPort, CartDTOMapper mapper) {
        this.cartPersistencePort = cartPersistencePort;
        this.stockFeignPort = stockFeignPort;
        this.transactionsFeignPort = transactionsFeignPort;
        this.reportFeignPort = reportFeignPort;
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
        updateTotalPriceOfCart(cart);

        save(cart);
    }

    private void updateTotalPriceOfCart(Cart cart) {
        if (cart.getCartItems().isEmpty()) {
            cart.setTotalPrice(BigDecimal.ZERO);
            return;
        }

        BigDecimal totalPrice = getTotalPriceFromArticles(getArticlesPriceDTO(cart.getCartItems()), cart.getCartItems());
        cart.setTotalPrice(totalPrice);
    }

    private BigDecimal getTotalPriceFromArticles(Set<ArticlesPriceDTO> articlesPriceDTOS, Set<CartItem> cartItems) {
        Map<Long, BigDecimal> mapArticlesToPrice = ParsingUtils.mapSetToMap(articlesPriceDTOS, ArticlesPriceDTO::getId, ArticlesPriceDTO::getPrice);

        return doTotalPriceOperation(mapArticlesToPrice, cartItems);
    }

    private BigDecimal doTotalPriceOperation(Map<Long, BigDecimal> mapArticlesToPrice, Set<CartItem> cartItems) {
        final BigDecimal[] totalPrice = {BigDecimal.ZERO};

        cartItems.forEach(item -> totalPrice[0] = totalPrice[0].add(mapArticlesToPrice.get(item.getArticleId()).multiply(BigDecimal.valueOf(item.getQuantity()))));

        return totalPrice[0];
    }

    private Set<ArticlesPriceDTO> getArticlesPriceDTO(Set<CartItem> cartItems) {
        return stockFeignPort.getArticlesPrice(getArticleIdsStringFromCartItems(cartItems));
    }

    private String getArticleIdsStringFromCartItems(Set<CartItem> cartItems) {
        return ParsingUtils.joinListElements(cartItems.stream().map(CartItem::getArticleId).toList());
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
        stockFeignPort.makeStockValidations(buildItemsReqDTO(cartReq));

        updateTotalPriceOfCart(cartReq);

        cartReq.setCreatedAt(LocalDateTime.now());
        save(cartReq);
    }

    private ItemsReqDTO buildItemsReqDTO(Cart cart) {
        return mapper.cartItemsToItemsReqDTO(cart.getCartItems());
    }

    @Override
    public PageDTO<ArticleResDTO> getAllCartItems(String direction, Integer pageSize, Integer page, String columns) {
        String articleIds = ParsingUtils.joinListElements(getAllArticleIdsForUserId());

        PageDTO<ArticleResDTO> articleRes = buildOrCreateArticlePageDTO(articleIds, direction, pageSize, page, columns);
        return validRes(articleRes);
    }

    private PageDTO<ArticleResDTO> validRes(PageDTO<ArticleResDTO> articleRes) {
        return articleRes == null ? null : buildFinalPageDTO(articleRes);
    }

    private PageDTO<ArticleResDTO> buildOrCreateArticlePageDTO(String articleIds, String direction, Integer pageSize, Integer page, String columns) {
        if (articleIds.isEmpty()) return null;

        return stockFeignPort.getPageableArticles(articleIds, direction, pageSize, page, columns);
    }


    private PageDTO<ArticleResDTO> buildFinalPageDTO(PageDTO<ArticleResDTO> articleRes) {
        Cart cart = findCartByUserId();
        CartPageDTO cartPage = cartToCartPage(cart);

        addCartQuantityToArticles(cart, articleRes.getContent());
        articleRes.setCart(cartPage);
        return articleRes;
    }

    private void addCartQuantityToArticles(Cart userCart, List<ArticleResDTO> articleRes) {
        Map<Long, Long> articleQuantityMap = mapItemsToQuantity(userCart.getCartItems());

        setCartQuantityForArticles(articleQuantityMap, articleRes);
    }

    private void setCartQuantityForArticles(Map<Long, Long> articleQuantityMap, List<ArticleResDTO> articleRes) {
        articleRes.forEach(a -> a.setCartQuantity(articleQuantityMap.get(a.getId())));
    }

    private CartPageDTO cartToCartPage(Cart cart) {
        return mapper.cartToCartPage(cart);
    }

    private List<Long> getAllArticleIdsForUserId() {
        return mapToArticleIds(findCartByUserId());
    }

    @Override
    public void buyCart() {
        Cart userCart = findCartByUserId();

        processStockValidations(userCart);
        processReductionInStock(userCart);
        processSaleTransaction(userCart);
        buildReport(userCart);
        deleteItemsFromCart(userCart);
        restoreTotalPrice(userCart);

        save(userCart);
    }

    private void restoreTotalPrice(Cart userCart) {
        userCart.setTotalPrice(BigDecimal.ZERO);
    }

    private void processReductionInStock(Cart userCart) {
        stockFeignPort.reduceStock(buildItemsReqDTO(userCart));
    }

    private void processSaleTransaction(Cart userCart) {
        SaleDTO saleDTO = buildSaleDTOFromCart(userCart);

        try {
            transactionsFeignPort.registerSale(saleDTO);
        } catch (Exception ex) {
            processRollbackOnStock(userCart);
            throw new PurchaseFailedException(Cart.class.getSimpleName(), CartEntity.Fields.userId, userCart.getUserId().toString());
        }
    }

    private void processRollbackOnStock(Cart userCart) {
        stockFeignPort.processRollbackOnStock(buildItemsReqDTO(userCart));
    }

    private SaleDTO buildSaleDTOFromCart(Cart cart) {
        return mapper.cartToSaleDTO(cart);
    }

    private void buildReport(Cart userCart) {
        List<ArticleResDTO> articles = getAllArticlesForCart(userCart);
        updateArticleQuantityWithCartInfo(userCart, articles);
        CartReportDTO cartReportDTO = buildCartReport(userCart, articles);

        reportFeignPort.generateReport(cartReportDTO);
    }

    private void updateArticleQuantityWithCartInfo(Cart userCart, List<ArticleResDTO> articles) {
        Map<Long, Long> articleQuantityMap = mapItemsToQuantity(userCart.getCartItems());
        setArticlesWithCartQuantity(articleQuantityMap, articles);
    }

    private void setArticlesWithCartQuantity(Map<Long, Long> articleQuantityMap, List<ArticleResDTO> articles) {
        articles.forEach(a -> a.setQuantity(articleQuantityMap.get(a.getId())));
    }

    private List<ArticleResDTO> getAllArticlesForCart(Cart cart) {
        return stockFeignPort.getAllArticles(buildItemsReqDTO(cart));
    }

    private CartReportDTO buildCartReport(Cart userCart, List<ArticleResDTO> articles) {
        return CartReportDTO.builder()
                .cartId(userCart.getId())
                .userId(userCart.getUserId())
                .cartLastUpdatedAt(userCart.getUpdatedAt())
                .totalPrice(userCart.getTotalPrice())
                .articles(new HashSet<>(articles))
                .build();
    }

    public void deleteItemsFromCart(Cart cart) {
        cart.deleteItems(cart.getCartItems());
    }

    private void processStockValidations(Cart userCart) {
        stockFeignPort.makeStockValidations(buildItemsReqDTO(userCart));
    }

    private List<Long> mapToArticleIds(Cart cart) {
        return cart.getCartItems().stream().map(CartItem::getArticleId).toList();
    }

    private void handleUpdateCart(Cart cartReq, Cart dbCart) {
        Set<CartItem> tentativeItems = getAllNewPossibleItemsOnCart(cartReq.getCartItems(), dbCart.getCartItems());

        stockFeignPort.makeStockValidations(mapper.cartItemsToItemsReqDTO(tentativeItems));

        dbCart.addItems(tentativeItems);
        updateTotalPriceOfCart(dbCart);

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
