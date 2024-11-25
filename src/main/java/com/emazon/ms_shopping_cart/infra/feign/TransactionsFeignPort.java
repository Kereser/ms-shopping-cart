package com.emazon.ms_shopping_cart.infra.feign;

import com.emazon.ms_shopping_cart.ConsUtils;
import com.emazon.ms_shopping_cart.application.dto.out.SaleDTO;
import com.emazon.ms_shopping_cart.infra.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = ConsUtils.MS_TRANSACTIONS, url = ConsUtils.MS_TRANSACTIONS_URL, configuration = FeignConfig.class)
public interface TransactionsFeignPort {

  @PostMapping(value = ConsUtils.WITH_SALES_URL, consumes = MediaType.APPLICATION_JSON_VALUE)
  void registerSale(SaleDTO sale);
}
