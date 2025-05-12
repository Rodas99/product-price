package com.product.price.services;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Service
public class SeleniumService {
    @Value("${webPage.url}")
    private String URL;

    private final ChromeOptions chromeOptions;

    public SeleniumService(ChromeOptions chromeOptions) {
        this.chromeOptions = chromeOptions;
    }

    public List<String> getPriceRetriever() throws IOException {
        WebDriver driver = new ChromeDriver(chromeOptions);

        System.out.println("Current URL: " + driver.getCurrentUrl());
        System.out.println("Page source:\n" + driver.getPageSource());

        try {
            driver.get(URL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            WebElement priceContainer = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("div.a-section.a-spacing-none.aok-align-center.aok-relative")
            ));

            WebElement priceWhole = priceContainer.findElement(By.className("a-price-whole"));
            String whole = priceWhole.getText();

            WebElement priceFraction = priceContainer.findElement(By.className("a-price-fraction"));
            String fraction = priceFraction.getText();

            String price = whole + "," + fraction;

            WebElement titleContainer = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("titleSection")
            ));

            WebElement productTitle = titleContainer.findElement(By.id("productTitle"));
            String title = productTitle.getText().split(" ")[0];

            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), Path.of("src/main/resources/screenshot.png"));

            return Arrays.asList(title, price);

        } finally {
            driver.quit();
        }
    }
}
