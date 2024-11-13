package com.emazon.ms_shopping_cart.infra.input.rest;

import com.emazon.ms_shopping_cart.ConsUtils;
import com.emazon.ms_shopping_cart.TestCreationUtils;
import com.emazon.ms_shopping_cart.application.dto.ItemsReqDTO;
import com.emazon.ms_shopping_cart.application.dto.handlers.PageDTO;
import com.emazon.ms_shopping_cart.application.dto.out.ArticleResDTO;
import com.emazon.ms_shopping_cart.domain.model.Cart;
import com.emazon.ms_shopping_cart.domain.spi.ICartPersistencePort;
import com.emazon.ms_shopping_cart.domain.spi.ReportFeignPort;
import com.emazon.ms_shopping_cart.domain.spi.StockFeignPort;
import com.emazon.ms_shopping_cart.domain.spi.TransactionsFeignPort;
import com.emazon.ms_shopping_cart.infra.exception.PurchaseFailedException;
import com.emazon.ms_shopping_cart.infra.exceptionhandler.ExceptionResponse;
import com.emazon.ms_shopping_cart.infra.out.jpa.entity.CartEntity;
import com.emazon.ms_shopping_cart.infra.out.jpa.entity.CartItemEntity;
import com.emazon.ms_shopping_cart.infra.security.model.CustomUserDetails;
import com.emazon.ms_shopping_cart.infra.security.utils.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Request;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class CartControllerIntegrationTest {
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper mapper;

  @MockBean private StockFeignPort stockFeignPort;

  @MockBean private ReportFeignPort reportFeignPort;

  @MockBean private TransactionsFeignPort transactionsFeignPort;

  @SpyBean private ICartPersistencePort cartPersistencePort;

  @PersistenceContext private EntityManager entityManager;

  @Test
  void Should_ThrowsException_When_NotAuthorized() throws Exception {
    mockMvc.perform(put(ConsUtils.builderPath().build()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void Should_ThrowsException_When_NotValidRole() throws Exception {
    mockMvc.perform(put(ConsUtils.builderPath().build())
        .header(ConsUtils.AUTHORIZATION, ConsUtils.BEARER + getAuxDepotToken()))
        .andExpect(status().isForbidden());
  }

  @Test
  void Should_ThrowsException_When_NotValidToken() throws Exception {
    mockMvc.perform(put(ConsUtils.builderPath().build(), ConsUtils.LONG_1)
        .header(ConsUtils.AUTHORIZATION, ConsUtils.BEARER + ConsUtils.BEARER))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void Should_ThrowsException_When_NotAvailableConnectionToStock() throws Exception {
    Mockito.doThrow(getFeignInternalError()).when(stockFeignPort).makeStockValidations(Mockito.any());

    mockMvc.perform(put(ConsUtils.builderPath().build())
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(TestCreationUtils.getItemsReqDTO()))
        .header(ConsUtils.AUTHORIZATION, ConsUtils.BEARER + getClientToken()))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void Should_ThrowsException_When_NotValidArticleId() throws Exception {
    Mockito.doThrow(getFeignBadRequest())
        .when(stockFeignPort).makeStockValidations(Mockito.any());

    mockMvc.perform(put(ConsUtils.builderPath().build())
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(TestCreationUtils.getItemsReqDTO()))
        .header(ConsUtils.AUTHORIZATION, ConsUtils.BEARER + getClientToken()))
        .andExpect(status().isBadRequest());
  }

  @Test
  void Should_ThrowsException_When_NotSufficientStock() throws Exception {
    Mockito.doThrow(getFeignConflicted())
        .when(stockFeignPort).makeStockValidations(Mockito.any());

    mockMvc.perform(put(ConsUtils.builderPath().build())
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(TestCreationUtils.getItemsReqDTO()))
        .header(ConsUtils.AUTHORIZATION, ConsUtils.BEARER + getClientToken()))
        .andExpect(status().isConflict());
  }

  @Test
  void Should_ThrowsException_When_ForbiddenAtFeign() throws Exception {
    Mockito.doThrow(getFeignForbidden())
        .when(stockFeignPort).makeStockValidations(Mockito.any());

    mockMvc.perform(put(ConsUtils.builderPath().build())
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(TestCreationUtils.getItemsReqDTO()))
        .header(ConsUtils.AUTHORIZATION, ConsUtils.BEARER + getClientToken()))
        .andExpect(status().isForbidden());
  }

  @Test
  void Should_ThrowsException_When_InvalidToken() throws Exception {
    Mockito.doThrow(getFeignForbidden())
        .when(stockFeignPort).makeStockValidations(Mockito.any());

    mockMvc.perform(put(ConsUtils.builderPath().build())
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(TestCreationUtils.getItemsReqDTO()))
        .header(ConsUtils.AUTHORIZATION, ConsUtils.BEARER + getAuxDepotToken()))
        .andExpect(status().isForbidden());
  }

  @Test
  void Should_Save_When_ValidationsWentWell() throws Exception {
    saveValidCart();

    List<CartEntity> cartEntityList = entityManager.createQuery(ConsUtils.GET_ALL_CARTS, CartEntity.class).getResultList();
    Assertions.assertEquals(ConsUtils.INTEGER_1, cartEntityList.size());
    Assertions.assertEquals(ConsUtils.LONG_1, cartEntityList.get(0).getId());
  }

  @Test
  void Should_UpdateCart_When_ValidationsWentWell() throws Exception {
    ItemsReqDTO dto = TestCreationUtils.getItemsReqDTO();
    saveValidCart(dto);

    List<CartEntity> cartEntityList = entityManager.createQuery(ConsUtils.GET_ALL_CARTS, CartEntity.class).getResultList();
    Assertions.assertEquals(ConsUtils.INTEGER_1, cartEntityList.size());
    Assertions.assertEquals(ConsUtils.LONG_1, cartEntityList.get(0).getCartItems().stream().findFirst().map(CartItemEntity::getQuantity).orElse(ConsUtils.LONG_0));

    dto.addAll(Set.of(TestCreationUtils.getCartItemDTO()));
    saveValidCart(dto);

    cartEntityList = entityManager.createQuery(ConsUtils.GET_ALL_CARTS, CartEntity.class).getResultList();
    Assertions.assertEquals(ConsUtils.INTEGER_1, cartEntityList.size());
    Assertions.assertEquals(ConsUtils.LONG_2, cartEntityList.get(0).getCartItems().stream().findFirst().map(CartItemEntity::getQuantity).orElse(ConsUtils.LONG_0));
  }

  /*** Delete item from cart ***/
  @Test
  void Should_ThrowsException_When_CartIdNotFound() throws Exception {
    mockMvc.perform(delete(ConsUtils.builderPath().withCartId().withArticles().withArticleId().build(), ConsUtils.LONG_1, ConsUtils.LONG_1)
        .header(ConsUtils.AUTHORIZATION, ConsUtils.BEARER + getClientToken()))
        .andExpect(jsonPath(ConsUtils.FIELD_MESSAGE).value(ExceptionResponse.ERROR_PROCESSING_OPERATION + Cart.class.getSimpleName()))
        .andExpect(jsonPath(ConsUtils.FIELD_ERRORS_ID).value(ExceptionResponse.ID_NOT_FOUND))
        .andExpect(status().isNotFound());
  }

  @Test
  void Should_ThrowsException_When_ArticleIdNotFound() throws Exception {
    saveValidCart();

    List<CartEntity> cartEntityList = entityManager.createQuery(ConsUtils.GET_ALL_CARTS, CartEntity.class).getResultList();
    Assertions.assertEquals(ConsUtils.INTEGER_1, cartEntityList.size());
    Assertions.assertEquals(ConsUtils.LONG_1, cartEntityList.get(ConsUtils.INTEGER_0).getCartItems().size());

    mockMvc.perform(delete(ConsUtils.builderPath().withCartId().withArticles().withArticleId().build(), ConsUtils.LONG_1, ConsUtils.LONG_2)
        .header(ConsUtils.AUTHORIZATION, ConsUtils.BEARER + getClientToken()))
        .andExpect(jsonPath(ConsUtils.FIELD_MESSAGE).value(ExceptionResponse.ERROR_PROCESSING_OPERATION + ConsUtils.ARTICLE_ENTITY))
        .andExpect(jsonPath(ConsUtils.FIELD_ERRORS_ID).value(ExceptionResponse.ID_NOT_FOUND))
        .andExpect(status().isNotFound());
  }

  @Test
  @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
  void Should_DeleteItemFromCart_When_ValidData() throws Exception {
    ItemsReqDTO dto = saveValidCart();

    Long articleId = dto.getItems().stream().findFirst().orElseThrow().getArticleId();
    Mockito.doReturn(Set.of(TestCreationUtils.createArticlePriceDTO(articleId))).when(stockFeignPort).getArticlesPrice(Mockito.any());

    mockMvc.perform(delete(ConsUtils.builderPath().withCartId().withArticles().withArticleId().build(), ConsUtils.LONG_1, articleId)
        .header(ConsUtils.AUTHORIZATION, ConsUtils.BEARER + getClientToken()))
        .andExpect(status().isOk());

    List<CartEntity> cartEntityList = entityManager.createQuery(ConsUtils.GET_ALL_CARTS, CartEntity.class).getResultList();
    Assertions.assertEquals(ConsUtils.INTEGER_1, cartEntityList.size());
    Assertions.assertEquals(ConsUtils.LONG_0, cartEntityList.get(ConsUtils.INTEGER_0).getCartItems().size());
  }

  /*** Get all cart items ***/
  @Test
  void Should_ThrowsException_When_CartIdNotFoundOnGetItems() throws Exception {
    saveValidCart();

    mockMvc.perform(get(ConsUtils.builderPath().withCartId().withArticles().build(), ConsUtils.LONG_2)
        .header(ConsUtils.AUTHORIZATION, ConsUtils.BEARER + getClientToken()))
        .andExpect(status().isNotFound());
  }

  @Test
  @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
  void Should_Get200_When_ValidPayload() throws Exception {
    saveValidCart();

    Mockito.doReturn(PageDTO.builder().content(List.of(TestCreationUtils.createArticleRes())).build()).when(stockFeignPort).getPageableArticles(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

    mockMvc.perform(get(ConsUtils.builderPath().withUser().withArticles().build(), ConsUtils.LONG_1)
        .header(ConsUtils.AUTHORIZATION, ConsUtils.BEARER + getClientToken()))
        .andExpect(status().isOk());
  }

  /*** Buy cart ***/
  @Test
  void Should_ThrowsException_When_PurchaseFailed() throws Exception {
    Cart cart = TestCreationUtils.createCart();
    Mockito.doReturn(Optional.of(cart)).when(cartPersistencePort).findByUserId(Mockito.any());

    ArticleResDTO articleResDTO = TestCreationUtils.createArticleRes(cart.getCartItems().stream().findFirst().orElseThrow().getArticleId());
    Mockito.doReturn(List.of(articleResDTO)).when(stockFeignPort).getAllArticles(Mockito.any());

    Mockito.doThrow(PurchaseFailedException.class).when(transactionsFeignPort).registerSale(Mockito.any());

    mockMvc.perform(post(ConsUtils.builderPath().withCheckout().build())
        .header(ConsUtils.AUTHORIZATION, ConsUtils.BEARER + getClientToken()))
        .andExpect(status().isPaymentRequired())
        .andExpect(jsonPath(ConsUtils.FIELD_MESSAGE).value(String.format(ExceptionResponse.PURCHASE_FAILED_MSG, cart.getUserId())));
  }

  @Test
  void Should_BuyCart_When_ValidScenario() throws Exception {
    Cart cart = TestCreationUtils.createCart();
    Mockito.doReturn(Optional.of(cart)).when(cartPersistencePort).findByUserId(Mockito.any());

    ArticleResDTO articleResDTO = TestCreationUtils.createArticleRes(cart.getCartItems().stream().findFirst().orElseThrow().getArticleId());
    Mockito.doReturn(List.of(articleResDTO)).when(stockFeignPort).getAllArticles(Mockito.any());

    mockMvc.perform(post(ConsUtils.builderPath().withCheckout().build())
        .header(ConsUtils.AUTHORIZATION, ConsUtils.BEARER + getClientToken()))
        .andExpect(status().isOk());
  }


  private ItemsReqDTO saveValidCart(ItemsReqDTO dto) throws Exception {
    Mockito.doNothing().when(stockFeignPort).makeStockValidations(Mockito.any());

    ItemsReqDTO itemsDTO = dto == null ? TestCreationUtils.getItemsReqDTO() : dto;
    Mockito.doReturn(TestCreationUtils.createArticlePriceDTOFromCartItems(itemsDTO.getItems())).when(stockFeignPort).getArticlesPrice(Mockito.any());

    mockMvc.perform(put(ConsUtils.builderPath().build())
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(itemsDTO))
        .header(ConsUtils.AUTHORIZATION, ConsUtils.BEARER + getClientToken()))
        .andExpect(status().isCreated());

    return itemsDTO;
  }

  private ItemsReqDTO saveValidCart() throws Exception {
    return saveValidCart(null);
  }

  private String getAuxDepotToken() {
    CustomUserDetails userDetail = new CustomUserDetails(ConsUtils.USERNAME,
        ConsUtils.PASSWORD,
        Set.of(new SimpleGrantedAuthority(ConsUtils.ROLE.concat(ConsUtils.AUX_DEPOT))),
        ConsUtils.LONG_1);

    return JwtUtils.createToken(new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities()));
  }

  private String getClientToken() {
    CustomUserDetails userDetail = new CustomUserDetails(ConsUtils.USERNAME,
        ConsUtils.PASSWORD,
        Set.of(new SimpleGrantedAuthority(ConsUtils.ROLE.concat(ConsUtils.CLIENT))),
        ConsUtils.LONG_1);
    return JwtUtils.createToken(new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities()));
  }

  private FeignException.BadRequest getFeignBadRequest() throws JsonProcessingException {
    return new FeignException.BadRequest(null,
        Request.create(Request.HttpMethod.PUT, ConsUtils.builderPath().build(), Map.of(), null, null, null),
        mapper.writeValueAsBytes(ExceptionResponse.builder().build()),
        null);
  }

  private FeignException.Conflict getFeignConflicted() throws JsonProcessingException {
    return new FeignException.Conflict(null,
        Request.create(Request.HttpMethod.PUT, ConsUtils.builderPath().build(), Map.of(), null, null, null),
        mapper.writeValueAsBytes(ExceptionResponse.builder().build()),
        null);
  }

  private FeignException.Forbidden getFeignForbidden() throws JsonProcessingException {
    return new FeignException.Forbidden(null,
        Request.create(Request.HttpMethod.PUT, ConsUtils.builderPath().build(), Map.of(), null, null, null),
        mapper.writeValueAsBytes(ExceptionResponse.builder().build()),
        null);
  }

  private FeignException.InternalServerError getFeignInternalError() throws JsonProcessingException {
    return new FeignException.InternalServerError(null,
        Request.create(Request.HttpMethod.PUT, ConsUtils.builderPath().build(), Map.of(), null, null, null),
        mapper.writeValueAsBytes(ExceptionResponse.builder().build()),
        null);
  }
}
