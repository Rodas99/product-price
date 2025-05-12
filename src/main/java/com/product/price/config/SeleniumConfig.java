package com.product.price.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.*;

@Configuration
public class SeleniumConfig {

    @PostConstruct
    void postConstructor() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        String driverName;

        if (os.contains("win")) {
            driverName = "drivers/chromedriver.exe";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            driverName = "drivers/chromedriver-linux";
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + os);
        }

        InputStream in = getClass().getClassLoader().getResourceAsStream(driverName);
        if (in == null) {
            throw new FileNotFoundException("Chromedriver not found in resources: " + driverName);
        }

        File tempFile = File.createTempFile("chromedriver", os.contains("win") ? ".exe" : "");
        tempFile.deleteOnExit();

        try (OutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }

        if (!os.contains("win")) {
            tempFile.setExecutable(true);
        }

        System.setProperty("webdriver.chrome.driver", tempFile.getAbsolutePath());
    }
}
