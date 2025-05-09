package com.product.price.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.*;

@Configuration
public class SeleniumConfig {

    @PostConstruct
    void postConstructor() throws IOException {
        // Copy chromedriver from resources to a temp file
        InputStream in = getClass().getClassLoader().getResourceAsStream("driver/chromedriver.exe");
        if (in == null) {
            throw new FileNotFoundException("Chromedriver not found in resources/driver/");
        }

        File tempFile = File.createTempFile("chromedriver", ".exe");
        tempFile.deleteOnExit();

        try (OutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }

        System.setProperty("webdriver.chrome.driver", tempFile.getAbsolutePath());
    }
}
