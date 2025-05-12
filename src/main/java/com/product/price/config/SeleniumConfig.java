package com.product.price.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeleniumConfig {

    @PostConstruct
    public void setupDriver() {
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
    }
}
