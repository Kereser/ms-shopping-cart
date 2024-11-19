package com.emazon.ms_shopping_cart.infra.config;

import com.emazon.ms_shopping_cart.application.mapper.CartDTOMapper;
import com.emazon.ms_shopping_cart.domain.api.ICartServicePort;
import com.emazon.ms_shopping_cart.domain.spi.ICartPersistencePort;
import com.emazon.ms_shopping_cart.infra.feign.ReportFeignPort;
import com.emazon.ms_shopping_cart.infra.feign.StockFeignPort;
import com.emazon.ms_shopping_cart.infra.feign.TransactionsFeignPort;
import com.emazon.ms_shopping_cart.domain.usecases.CartUseCase;
import com.emazon.ms_shopping_cart.infra.out.jpa.adapter.CartJpaAdapter;
import com.emazon.ms_shopping_cart.infra.out.jpa.mapper.CartEntityMapper;
import com.emazon.ms_shopping_cart.infra.out.jpa.repository.CartJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfig {

  private final StockFeignPort stockFeignPort;
  private final CartEntityMapper cartEntityMapper;
  private final CartDTOMapper cartDTOMapper;
  private final CartJpaRepository cartJpaRepository;
  private final TransactionsFeignPort transactionsFeignPort;
  private final ReportFeignPort reportFeignPort;

  @Bean
  public ICartServicePort cartServicePort() {
    return new CartUseCase(cartPersistencePort(), stockFeignPort, transactionsFeignPort, reportFeignPort, cartDTOMapper);
  }

  @Bean
  public ICartPersistencePort cartPersistencePort() {
    return new CartJpaAdapter(cartJpaRepository, cartEntityMapper);
  }
}
