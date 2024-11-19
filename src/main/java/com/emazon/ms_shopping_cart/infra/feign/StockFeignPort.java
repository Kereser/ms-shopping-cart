package com.emazon.ms_shopping_cart.infra.feign;

import com.emazon.ms_shopping_cart.ConsUtils;
import com.emazon.ms_shopping_cart.application.dto.ItemsReqDTO;
import com.emazon.ms_shopping_cart.application.dto.handlers.PageDTO;
import com.emazon.ms_shopping_cart.application.dto.input.ArticlesPriceDTO;
import com.emazon.ms_shopping_cart.application.dto.out.ArticleResDTO;
import com.emazon.ms_shopping_cart.infra.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@FeignClient(name = ConsUtils.MS_STOCK, url = ConsUtils.MS_STOCK_URL, configuration = FeignConfig.class)
public interface StockFeignPort {

  @PostMapping(value = ConsUtils.VALIDATE_STOCK_FOR_CART, consumes = MediaType.APPLICATION_JSON_VALUE)
  void makeStockValidations(ItemsReqDTO dto);

  @GetMapping(value = ConsUtils.GET_ALL_ITEMS_FROM_STOCK, consumes = MediaType.APPLICATION_JSON_VALUE)
  PageDTO<ArticleResDTO> getPageableArticles(@PathVariable String articleIds, @RequestParam String direction, @RequestParam Integer pageSize, @RequestParam Integer page, @RequestParam String categoryName, @RequestParam String brandName);

  @GetMapping(value = ConsUtils.GET_ITEMS_WITH_PRICE, consumes = MediaType.APPLICATION_JSON_VALUE)
  Set<ArticlesPriceDTO> getArticlesPrice(@PathVariable String articleIds);

  @PostMapping(value = ConsUtils.PROCESS_PURCHASE_ON_STOCK, consumes = MediaType.APPLICATION_JSON_VALUE)
  void reduceStock(ItemsReqDTO itemsReqDTO);

  @PostMapping(value = ConsUtils.GET_ALL_ARTICLES_FROM_STOCK, consumes = MediaType.APPLICATION_JSON_VALUE)
  List<ArticleResDTO> getAllArticles(ItemsReqDTO itemsReqDTO);

  @PostMapping(value = ConsUtils.PROCESS_CART_ROLLBACK_URL, consumes = MediaType.APPLICATION_JSON_VALUE)
  void processRollbackOnStock(ItemsReqDTO itemsReqDTO);
}
