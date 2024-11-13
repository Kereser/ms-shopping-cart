package com.emazon.ms_shopping_cart.domain.usecases;

import com.emazon.ms_shopping_cart.ConsUtils;
import com.emazon.ms_shopping_cart.TestCreationUtils;
import com.emazon.ms_shopping_cart.application.dto.handlers.PageDTO;
import com.emazon.ms_shopping_cart.application.dto.out.ArticleResDTO;
import com.emazon.ms_shopping_cart.application.dto.out.CartPageDTO;
import com.emazon.ms_shopping_cart.application.mapper.CartDTOMapper;
import com.emazon.ms_shopping_cart.domain.model.Cart;
import com.emazon.ms_shopping_cart.domain.spi.ICartPersistencePort;
import com.emazon.ms_shopping_cart.domain.spi.StockFeignPort;
import com.emazon.ms_shopping_cart.infra.exception.NoDataFoundException;
import com.emazon.ms_shopping_cart.infra.security.model.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class CartUseCaseTest {

  @Mock private ICartPersistencePort cartPersistencePort;

  @Mock private StockFeignPort stockFeignPort;

  @Mock private CartDTOMapper mapper;

  @InjectMocks private CartUseCase cartUseCase;

  private static final CustomUserDetails USER_DETAILS_ID_1 = new CustomUserDetails(ConsUtils.USERNAME,
      ConsUtils.PASSWORD,
      Set.of(new SimpleGrantedAuthority(ConsUtils.ROLE.concat(ConsUtils.CLIENT))),
      ConsUtils.LONG_1);

  private static final CartPageDTO CART_PAGE_DTO = new CartPageDTO(ConsUtils.LONG_1, ConsUtils.LONG_1, LocalDateTime.now(), LocalDateTime.now(), BigDecimal.TEN);
  private static final PageDTO<Object> ARTICLE_RES_DTO_PAGE_DTO = PageDTO.builder()
      .content(List.of(ArticleResDTO.builder().build()))
      .build();

  @AfterEach
  void tearDown() {
    Mockito.reset(cartPersistencePort);
  }

  @Test
  void Should_SaveNewCart_When_ValidPayload() {
    Mockito.doReturn(USER_DETAILS_ID_1).when(cartPersistencePort).getSecurityPrincipal();
    Cart cart = TestCreationUtils.createCart();

    Long articlePriceId = cart.getCartItems().stream().findFirst().orElseThrow().getArticleId();
    Mockito.doReturn(Set.of(TestCreationUtils.createArticlePriceDTO(articlePriceId))).when(stockFeignPort).getArticlesPrice(Mockito.any());

    cartUseCase.handleAddOperation(cart);

    Mockito.verify(cartPersistencePort, Mockito.times(ConsUtils.INTEGER_1)).getSecurityPrincipal();
    Mockito.verify(cartPersistencePort, Mockito.times(ConsUtils.INTEGER_1)).findByUserId(cart.getUserId());
  }

  @Test
  void Should_deleteItemsFromCart_When_ValidPayload() {
    Mockito.doReturn(USER_DETAILS_ID_1).when(cartPersistencePort).getSecurityPrincipal();
    Cart cart = TestCreationUtils.createCart();
    Mockito.doReturn(Optional.of(cart)).when(cartPersistencePort).findByUserId(Mockito.any());

    Long articlePriceId = cart.getCartItems().stream().findFirst().orElseThrow().getArticleId();
    Mockito.doReturn(Set.of(TestCreationUtils.createArticlePriceDTO(articlePriceId))).when(stockFeignPort).getArticlesPrice(Mockito.any());

    cartUseCase.handleAddOperation(cart);

    Mockito.verify(cartPersistencePort, Mockito.times(ConsUtils.INTEGER_1)).getSecurityPrincipal();
    Mockito.verify(cartPersistencePort, Mockito.times(ConsUtils.INTEGER_1)).findByUserId(Mockito.any());
  }

  @Test
  void Should_DeleteArticle_When_ValidPayload() {
    Mockito.doReturn(USER_DETAILS_ID_1).when(cartPersistencePort).getSecurityPrincipal();
    Cart cart = TestCreationUtils.createCart();
    Mockito.doReturn(Optional.of(cart)).when(cartPersistencePort).findByUserId(ConsUtils.LONG_1);

    Long articleId = cart.getCartItems().stream().findFirst().orElseThrow().getArticleId();
    cartUseCase.deleteArticleFromCart(ConsUtils.LONG_1, articleId);

    Mockito.verify(cartPersistencePort, Mockito.times(ConsUtils.INTEGER_1)).getSecurityPrincipal();
    Mockito.verify(cartPersistencePort, Mockito.times(ConsUtils.INTEGER_1)).findByUserId(Mockito.any());
  }

  @Test
  void Should_ThrowsException_When_CartIdNotFound() {
    Mockito.doReturn(USER_DETAILS_ID_1).when(cartPersistencePort).getSecurityPrincipal();
    Mockito.doReturn(Optional.empty()).when(cartPersistencePort).findByUserId(Mockito.any());

    Assertions.assertThrows(NoDataFoundException.class, () -> cartUseCase.deleteArticleFromCart(ConsUtils.LONG_1, ConsUtils.LONG_1));

    Mockito.verify(cartPersistencePort, Mockito.times(ConsUtils.INTEGER_1)).getSecurityPrincipal();
    Mockito.verify(cartPersistencePort, Mockito.times(ConsUtils.INTEGER_1)).findByUserId(Mockito.any());
  }

  @Test
  void Should_ThrowsException_When_ArticleIdNotFound() {
    Mockito.doReturn(USER_DETAILS_ID_1).when(cartPersistencePort).getSecurityPrincipal();
    Cart cart = TestCreationUtils.createCart();
    Mockito.doReturn(Optional.of(cart)).when(cartPersistencePort).findByUserId(ConsUtils.LONG_1);

    Long nonValidArticleId = cart.getCartItems().stream().findFirst().orElseThrow().getArticleId() + ConsUtils.LONG_1;

    Assertions.assertThrows(NoDataFoundException.class, () -> cartUseCase.deleteArticleFromCart(ConsUtils.LONG_1, nonValidArticleId));

    Mockito.verify(cartPersistencePort, Mockito.times(ConsUtils.INTEGER_1)).getSecurityPrincipal();
    Mockito.verify(cartPersistencePort, Mockito.times(ConsUtils.INTEGER_1)).findByUserId(cart.getUserId());
  }

  /*** Cart pageable ***/
  @Test
  void Should_CorrectlyInteracts_When_GetAllCartItems() {
    Mockito.doReturn(TestCreationUtils.buildUserDetails()).when(cartPersistencePort).getSecurityPrincipal();
    Cart cart = TestCreationUtils.createCart();
    Mockito.doReturn(Optional.of(cart)).when(cartPersistencePort).findByUserId(Mockito.any());
    Mockito.doReturn(ARTICLE_RES_DTO_PAGE_DTO).when(stockFeignPort).getPageableArticles(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    Mockito.doReturn(CART_PAGE_DTO).when(mapper).cartToCartPage(Mockito.any());

    cartUseCase.getAllCartItems(Sort.Direction.ASC.name(), ConsUtils.INTEGER_20, ConsUtils.INTEGER_0, ConsUtils.NAME, ConsUtils.NAME);

    Mockito.verify(cartPersistencePort, Mockito.times(ConsUtils.INTEGER_2)).findByUserId(Mockito.any());
    Mockito.verify(cartPersistencePort, Mockito.times(ConsUtils.INTEGER_2)).getSecurityPrincipal();
    Mockito.verify(stockFeignPort, Mockito.times(ConsUtils.INTEGER_1)).getPageableArticles(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
  }

  @Test
  void Should_ThrowsException_When_CartIdNotFoundOnGetAll() {
    Mockito.reset(cartPersistencePort);
    Mockito.doReturn(TestCreationUtils.buildUserDetails()).when(cartPersistencePort).getSecurityPrincipal();
    Mockito.doReturn(Optional.empty()).when(cartPersistencePort).findByUserId(Mockito.any());

    String direction = Sort.Direction.ASC.name();
    Assertions.assertThrows(NoDataFoundException.class, () -> cartUseCase.getAllCartItems(direction, ConsUtils.INTEGER_20, ConsUtils.INTEGER_0, ConsUtils.NAME, ConsUtils.NAME));
  }

  @Test
  void save() {
    Cart cart = TestCreationUtils.createCart();
    cartUseCase.save(cart);
    Mockito.verify(cartPersistencePort, Mockito.times(ConsUtils.INTEGER_1)).save(cart);
  }
}
