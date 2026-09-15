package cl.duoc.pedidos360.bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
public class OrdersClientConfig {

    @Bean
    @Primary
    public RestClient ordersRestClient(
            @Value("${services.orders.base-url}")
            String ordersBaseUrl) {

        return RestClient
            .builder()
            .baseUrl(ordersBaseUrl)
            .build();
    }
}