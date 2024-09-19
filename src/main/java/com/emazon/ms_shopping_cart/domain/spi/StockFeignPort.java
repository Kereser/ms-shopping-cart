package com.emazon.ms_shopping_cart.domain.spi;

import com.emazon.ms_shopping_cart.application.dto.ItemsReqDTO;
import com.emazon.ms_shopping_cart.infra.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "MS-STOCK", url = "${external.feign.url.ms-stock}", configuration = FeignConfig.class)
public interface StockFeignPort {

    @PutMapping(value = "/articles", consumes = MediaType.APPLICATION_JSON_VALUE)
    void handleAdditionToCart(ItemsReqDTO dto);
}
