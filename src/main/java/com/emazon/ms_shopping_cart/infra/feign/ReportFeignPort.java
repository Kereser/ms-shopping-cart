package com.emazon.ms_shopping_cart.infra.feign;

import com.emazon.ms_shopping_cart.ConsUtils;
import com.emazon.ms_shopping_cart.application.dto.out.CartReportDTO;
import com.emazon.ms_shopping_cart.infra.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = ConsUtils.MS_REPORT, url = ConsUtils.MS_REPORT_URL, configuration = FeignConfig.class)
public interface ReportFeignPort {

  @PostMapping
  void generateReport(CartReportDTO cartReportDTO);
}
