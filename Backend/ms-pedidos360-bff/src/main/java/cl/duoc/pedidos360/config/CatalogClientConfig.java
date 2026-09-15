package cl.duoc.pedidos360.bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class CatalogClientConfig {

    @Bean("catalogRestClient")
    public RestClient catalogRestClient(
            @Value("${services.catalog.base-url}")
            String catalogBaseUrl) {

        return RestClient
            .builder()
            .baseUrl(catalogBaseUrl)
            .build();
    }
}