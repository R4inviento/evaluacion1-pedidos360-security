package cl.duoc.pedidos360.bff.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/reports")
public class ReportProxyController {

    private final RestClient reportRestClient;

    public ReportProxyController(
            @Qualifier("reportRestClient")
            RestClient reportRestClient) {

        this.reportRestClient =
            reportRestClient;
    }

    @GetMapping("/summary")
    @PreAuthorize(
        "hasAnyRole('Admin', 'Operator')"
    )
    public ResponseEntity<String> getSummary() {

        return reportRestClient
            .get()
            .uri("/api/reports/summary")
            .retrieve()
            .toEntity(String.class);
    }
}