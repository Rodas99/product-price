package com.product.price.web;

import com.product.price.models.ProductPriceDto;
import com.product.price.services.SheetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
public class PriceController {

    private final SheetsService service;

    @Autowired
    public PriceController(SheetsService service) {
        this.service = service;
    }

    @GetMapping("/run-price-controller")
    public ResponseEntity<String> runTask(@RequestParam String token) {

        if (!"mySecretToken123".equals(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            service.updateSheet();
            return ResponseEntity.ok("Task executed successfully.");
        } catch (URISyntaxException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error executing task: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred.");
        }
    }

    @PostMapping("/run-price-controller-pa")
    public ResponseEntity<String> runTaskV2(@RequestParam String token,
                                            @RequestBody ProductPriceDto productPriceDto) {

        if (!"mySecretToken123".equals(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            service.updateSheetPA(productPriceDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred.");
        }

        return ResponseEntity.ok("Task executed successfully.");

    }

}
