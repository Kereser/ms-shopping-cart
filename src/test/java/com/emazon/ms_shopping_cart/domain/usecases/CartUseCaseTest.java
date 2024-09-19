package com.emazon.ms_shopping_cart.domain.usecases;

import com.emazon.ms_shopping_cart.ConsUtils;
import com.emazon.ms_shopping_cart.application.mapper.CartDTOMapper;
import com.emazon.ms_shopping_cart.domain.model.Cart;
import com.emazon.ms_shopping_cart.domain.model.CartItem;
import com.emazon.ms_shopping_cart.domain.spi.ICartPersistencePort;
import com.emazon.ms_shopping_cart.domain.spi.StockFeignPort;
import com.emazon.ms_shopping_cart.infra.exception.ResourceOwnershipViolationException;
import com.emazon.ms_shopping_cart.infra.security.model.CustomUserDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class CartUseCaseTest {

    @Mock
    private ICartPersistencePort cartPersistencePort;

    @Mock
    private StockFeignPort stockFeignPort;

    @Mock
    private CartDTOMapper mapper;

    @InjectMocks
    private CartUseCase cartUseCase;

    private static final CartItem CART_ITEM_SET = new CartItem(null, ConsUtils.LONG_1, ConsUtils.LONG_1);
    private static final CartItem CART_ITEM_SET_2 = new CartItem(null, ConsUtils.LONG_2, ConsUtils.LONG_1);

    private static final Cart CART = new Cart(ConsUtils.LONG_1, ConsUtils.LONG_1, new HashSet<>(Set.of(CART_ITEM_SET)), LocalDateTime.now(), LocalDateTime.now());
    private static final Cart CART_WITH_2_ITEMS = new Cart(ConsUtils.LONG_1, ConsUtils.LONG_1, new HashSet<>(Set.of(CART_ITEM_SET_2, CART_ITEM_SET)), LocalDateTime.now(), LocalDateTime.now());

    private static final CustomUserDetails USER_DETAILS_ID_1 = new CustomUserDetails(ConsUtils.USERNAME,
            ConsUtils.PASSWORD,
            Set.of(new SimpleGrantedAuthority(ConsUtils.ROLE.concat(ConsUtils.CLIENT))),
            ConsUtils.LONG_1);

    private static final CustomUserDetails USER_DETAILS_ID_2 = new CustomUserDetails(ConsUtils.USERNAME,
            ConsUtils.PASSWORD,
            Set.of(new SimpleGrantedAuthority(ConsUtils.ROLE.concat(ConsUtils.CLIENT))),
            ConsUtils.LONG_2);

    @Test
    void Should_SaveNewCart_When_ValidPayload() {
        Mockito.doReturn(USER_DETAILS_ID_1).when(cartPersistencePort).getSecurityContext();
        cartUseCase.handleAddOperation(CART);

        Mockito.verify(cartPersistencePort, Mockito.times(ConsUtils.INTEGER_1)).getSecurityContext();
        Mockito.verify(cartPersistencePort, Mockito.times(ConsUtils.INTEGER_1)).findByUserId(CART.getUserId());
    }

    @Test
    void Should_UpdateCart_When_ValidPayload() {
        Mockito.doReturn(USER_DETAILS_ID_1).when(cartPersistencePort).getSecurityContext();
        Mockito.doReturn(Optional.of(CART)).when(cartPersistencePort).findByUserId(CART.getUserId());

        cartUseCase.handleAddOperation(CART_WITH_2_ITEMS);

        Mockito.verify(cartPersistencePort, Mockito.times(ConsUtils.INTEGER_1)).getSecurityContext();
        Mockito.verify(cartPersistencePort, Mockito.times(ConsUtils.INTEGER_1)).findByUserId(CART.getUserId());
    }

    @Test
    void Should_ThrowsException_When_ResourceOwnerViolation() {
        Mockito.doReturn(USER_DETAILS_ID_2).when(cartPersistencePort).getSecurityContext();

        Assertions.assertThrows(ResourceOwnershipViolationException.class, () -> cartUseCase.handleAddOperation(CART_WITH_2_ITEMS));

        Mockito.verify(cartPersistencePort, Mockito.times(ConsUtils.INTEGER_1)).getSecurityContext();
        Mockito.verify(cartPersistencePort, Mockito.times(ConsUtils.INTEGER_0)).findByUserId(CART.getUserId());
    }

    @Test
    void save() {
        cartUseCase.save(CART);
        Mockito.verify(cartPersistencePort, Mockito.times(ConsUtils.INTEGER_1)).save(CART);
    }
}