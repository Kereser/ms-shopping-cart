package com.emazon.ms_shopping_cart.infra.input.rest;

import com.emazon.ms_shopping_cart.ConsUtils;
import com.emazon.ms_shopping_cart.application.dto.CartItemDTO;
import com.emazon.ms_shopping_cart.application.dto.ItemsReqDTO;
import com.emazon.ms_shopping_cart.domain.spi.StockFeignPort;
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
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class CartControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @SpyBean
    private StockFeignPort stockFeignPort;

    @PersistenceContext
    private EntityManager entityManager;

    private static final String USER = "testUser";
    private static final String AUX_DEPOT = "AUX_DEPOT";
    private static final String CLIENT = "CLIENT";
    private static final Long USER_ID = 1L;
    private static final Long ARTICLE_ID = 1L;
    private static final String PASSWORD = "password";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    private static final Set<CartItemDTO> cartItemsDTO = Set.of(CartItemDTO.builder().articleId(ARTICLE_ID).quantity(ConsUtils.LONG_1).build());
    private static final ItemsReqDTO itemsReqDTO = ItemsReqDTO.builder()
            .items(new HashSet<>(cartItemsDTO))
            .build();

    @Test
    void Should_ThrowsException_When_NotAuthorized() throws Exception {
        mockMvc.perform(put(ConsUtils.builderPath().withUserId().build(), USER_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void Should_ThrowsException_When_NotValidRole() throws Exception {
        mockMvc.perform(put(ConsUtils.builderPath().withUserId().build(), USER_ID)
                        .header(AUTHORIZATION, BEARER + getAuxDepotToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void Should_ThrowsException_When_NotValidToken() throws Exception {
        mockMvc.perform(put(ConsUtils.builderPath().withUserId().build(), USER_ID)
                        .header(AUTHORIZATION, BEARER + BEARER))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void Should_ThrowsException_When_NotAvailableConnectionToStock() throws Exception {
        mockMvc.perform(put(ConsUtils.builderPath().withUserId().build(), USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemsReqDTO))
                        .header(AUTHORIZATION, BEARER + getClientToken()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void Should_ThrowsException_When_NotValidArticleId() throws Exception {
        Mockito.doThrow(getFeignBadRequest())
                .when(stockFeignPort).handleAdditionToCart(Mockito.any());

        mockMvc.perform(put(ConsUtils.builderPath().withUserId().build(), USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemsReqDTO))
                        .header(AUTHORIZATION, BEARER + getClientToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void Should_ThrowsException_When_ResourceOwnershipViolation() throws Exception {
        mockMvc.perform(put(ConsUtils.builderPath().withUserId().build(), ConsUtils.LONG_2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemsReqDTO))
                        .header(AUTHORIZATION, BEARER + getClientToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void Should_ThrowsException_When_NotSufficientStock() throws Exception {
        Mockito.doThrow(getFeignConflicted())
                .when(stockFeignPort).handleAdditionToCart(Mockito.any());

        mockMvc.perform(put(ConsUtils.builderPath().withUserId().build(), USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemsReqDTO))
                        .header(AUTHORIZATION, BEARER + getClientToken()))
                .andExpect(status().isConflict());
    }

    @Test
    void Should_ThrowsException_When_ForbiddenAtFeign() throws Exception {
        Mockito.doThrow(getFeignForbidden())
                .when(stockFeignPort).handleAdditionToCart(Mockito.any());

        mockMvc.perform(put(ConsUtils.builderPath().withUserId().build(), USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemsReqDTO))
                        .header(AUTHORIZATION, BEARER + getClientToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void Should_ThrowsException_When_InvalidToken() throws Exception {
        Mockito.doThrow(getFeignForbidden())
                .when(stockFeignPort).handleAdditionToCart(Mockito.any());

        mockMvc.perform(put(ConsUtils.builderPath().withUserId().build(), USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemsReqDTO))
                        .header(AUTHORIZATION, BEARER + getAuxDepotToken()))
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
        saveValidCart();

        List<CartEntity> cartEntityList = entityManager.createQuery(ConsUtils.GET_ALL_CARTS, CartEntity.class).getResultList();
        Assertions.assertEquals(ConsUtils.INTEGER_1, cartEntityList.size());
        Assertions.assertEquals(ConsUtils.LONG_1, cartEntityList.get(0).getCartItems().stream().findFirst().map(CartItemEntity::getQuantity).orElse(ConsUtils.LONG_0));

        itemsReqDTO.getItems().add(CartItemDTO.builder().articleId(ConsUtils.LONG_2).quantity(ConsUtils.LONG_1).build());
        saveValidCart();

        cartEntityList = entityManager.createQuery(ConsUtils.GET_ALL_CARTS, CartEntity.class).getResultList();
        Assertions.assertEquals(ConsUtils.INTEGER_1, cartEntityList.size());
        Assertions.assertEquals(ConsUtils.LONG_2, cartEntityList.get(0).getCartItems().size());
    }

    private void saveValidCart() throws Exception {
        Mockito.doNothing().when(stockFeignPort).handleAdditionToCart(Mockito.any());

        mockMvc.perform(put(ConsUtils.builderPath().withUserId().build(), USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemsReqDTO))
                        .header(AUTHORIZATION, BEARER + getClientToken()))
                .andExpect(status().isCreated());
    }

    private String getAuxDepotToken() {
        CustomUserDetails userDetail = new CustomUserDetails(USER, PASSWORD, Set.of(new SimpleGrantedAuthority("ROLE_".concat(AUX_DEPOT))), USER_ID);
        return JwtUtils.createToken(new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities()));
    }

    private String getClientToken() {
        CustomUserDetails userDetail = new CustomUserDetails(USER, PASSWORD, Set.of(new SimpleGrantedAuthority("ROLE_".concat(CLIENT))), USER_ID);
        return JwtUtils.createToken(new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities()));
    }

    private FeignException.BadRequest getFeignBadRequest() throws JsonProcessingException {
        return new FeignException.BadRequest(null,
                Request.create(Request.HttpMethod.PUT, ConsUtils.builderPath().withUserId().build(), Map.of(), null, null, null),
                mapper.writeValueAsBytes(ExceptionResponse.builder().build()),
                null);
    }

    private FeignException.Conflict getFeignConflicted() throws JsonProcessingException {
        return new FeignException.Conflict(null,
                Request.create(Request.HttpMethod.PUT, ConsUtils.builderPath().withUserId().build(), Map.of(), null, null, null),
                mapper.writeValueAsBytes(ExceptionResponse.builder().build()),
                null);
    }

    private FeignException.Forbidden getFeignForbidden() throws JsonProcessingException {
        return new FeignException.Forbidden(null,
                Request.create(Request.HttpMethod.PUT, ConsUtils.builderPath().withUserId().build(), Map.of(), null, null, null),
                mapper.writeValueAsBytes(ExceptionResponse.builder().build()),
                null);
    }
}