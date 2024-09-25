package com.emazon.ms_shopping_cart.infra.input.rest;

import com.emazon.ms_shopping_cart.ConsUtils;
import com.emazon.ms_shopping_cart.application.dto.CartItemDTO;
import com.emazon.ms_shopping_cart.application.dto.ItemsReqDTO;
import com.emazon.ms_shopping_cart.application.handler.ICartHandler;
import com.emazon.ms_shopping_cart.infra.exceptionhandler.ExceptionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CartControllerUnitTest {

    @MockBean
    private ICartHandler cartHandler;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    /*** Add item from cart ***/
    @Test
    @WithMockUser(roles = ConsUtils.CLIENT)
    void Should_ThrowsException_When_NotValidFields() throws Exception {
        mockMvc.perform(put(ConsUtils.builderPath().build())
                        .content(mapper.writeValueAsString(ItemsReqDTO.builder().build()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(ConsUtils.FIELD_MESSAGE).value(ExceptionResponse.FIELD_VALIDATION_ERRORS))
                .andExpect(jsonPath(ConsUtils.FIELD_ITEMS).value(ExceptionResponse.NOT_NULL))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put(ConsUtils.builderPath().build())
                        .content(mapper.writeValueAsString(ItemsReqDTO.builder().items(Set.of(CartItemDTO.builder().build())).build()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(ConsUtils.FIELD_MESSAGE).value(ExceptionResponse.FIELD_VALIDATION_ERRORS))
                .andExpect(jsonPath(ConsUtils.FIELD_ARTICLE_ID_PATH_ARRAY).value(ExceptionResponse.NOT_NULL))
                .andExpect(jsonPath(ConsUtils.FIELD_QUANTITY_PATH_ARRAY).value(ExceptionResponse.NOT_NULL))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = ConsUtils.CLIENT)
    void Should_ThrowsException_When_NotBody() throws Exception {
        mockMvc.perform(put(ConsUtils.builderPath().build()))
                .andExpect(jsonPath(ConsUtils.FIELD_MESSAGE).value(ConsUtils.REQUIRED_BODY))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = ConsUtils.ADMIN)
    void Should_ThrowsException_When_NotValidRole() throws Exception {
        mockMvc.perform(put(ConsUtils.builderPath().build()))
                .andExpect(status().isForbidden());
    }

    @Test
    void Should_ThrowsException_When_NotAuthenticated() throws Exception {
        mockMvc.perform(put(ConsUtils.builderPath().build()))
                .andExpect(status().isUnauthorized());
    }

    /*** Delete item from cart ***/
    @Test
    @WithMockUser(roles = ConsUtils.ADMIN)
    void Should_ThrowsException_When_NotValidRoleOnDeletion() throws Exception {
        mockMvc.perform(delete(ConsUtils.builderPath().withCartId().withArticles().withArticleId().build(), ConsUtils.LONG_1, ConsUtils.LONG_1))
                .andExpect(status().isForbidden());
    }

    @Test
    void Should_ThrowsException_When_NotAuthenticatedOnDeletion() throws Exception {
        mockMvc.perform(delete(ConsUtils.builderPath().withCartId().withArticles().withArticleId().build(), ConsUtils.LONG_1, ConsUtils.LONG_1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = ConsUtils.CLIENT)
    void Should_ThrowsException_When_NotParamsOnDeletion() throws Exception {
        mockMvc.perform(delete(ConsUtils.builderPath().withCartId().withArticles().withArticleId().build(), ConsUtils.LONG_1, ConsUtils.LONG_1))
                .andExpect(status().isOk());
    }

    /*** Get all cart items ***/
    @Test
    void Should_ThrowsException_When_NotValidReqParam() throws Exception {
        mockMvc.perform(get(ConsUtils.builderPath().withCartId().withArticles().build(), ConsUtils.LONG_1)
                        .param(ConsUtils.DIRECTION_PARAM, ConsUtils.WRONG_SORT_PARAM))
                .andExpect(jsonPath(ConsUtils.FIELD_MESSAGE).value(ExceptionResponse.NOT_VALID_PARAM))
                .andExpect(jsonPath(ConsUtils.FIELD_DIRECTION).value(ConsUtils.WRONG_SORT_PARAM))
                .andExpect(status().isBadRequest());
    }
}