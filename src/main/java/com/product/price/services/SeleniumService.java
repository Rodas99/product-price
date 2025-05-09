package com.product.price.services;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Service
public class SeleniumService {

    @Value("${webPage.url}")
    private String URL;

    public List<String> getPriceRetriever() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-agent=Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--window-size=1920,1080");
        WebDriver driver = new ChromeDriver(options);

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

            return Arrays.asList(title, price);

        } finally {
            driver.quit();
        }
    }
}
