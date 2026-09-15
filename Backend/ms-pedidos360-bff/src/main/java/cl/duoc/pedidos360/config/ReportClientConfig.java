package cl.duoc.pedidos360.bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ReportClientConfig {

    @Bean("reportRestClient")
    public RestClient reportRestClient(
            @Value("${services.report.base-url}")
            String reportBaseUrl) {

        return RestClient
            .builder()
            .baseUrl(reportBaseUrl)
            .build();
    }
}