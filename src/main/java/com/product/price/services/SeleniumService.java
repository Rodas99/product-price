package com.product.price.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

        ((JavascriptExecutor) driver).executeScript(
                "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})"
        );

        try {

            driver.get(URL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page source:\n" + driver.getPageSource());

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

    public List<String> getPriceRetrieverApi() throws URISyntaxException, IOException {
        String url = "https://www.amazon.es/-/pt/gp/product/B092KKLH93/ref=ox_sc_act_title_1?smid=APCX7ZHLZ1CYN&psc=1";
        String apikey = "8d6e294ba13754ba8fc6808b1867c3c77f62961f";

        URI uri = new URIBuilder("https://ecommerce.api.zenrows.com/v1/targets/amazon/products/")
                .addParameter("apikey", apikey)
                .addParameter("url", url)
                .addParameter("country", "es")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(Request.get(uri)
                .execute().returnContent().asString());

        return Arrays.asList(root.path("product_name").asText(), root.path("product_price").asText());
    }
}
