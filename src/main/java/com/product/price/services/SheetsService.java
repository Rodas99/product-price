package com.product.price.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.product.price.oauth.OAuth.getCredentials;
import static com.product.price.oauth.OAuth.loadStoredCredentialFromEnv;

@Service
@EnableScheduling
public class SheetsService {

    private static final Logger log = LoggerFactory.getLogger(SheetsService.class);

    private final SeleniumService seleniumService;
    private static final String APPLICATION_NAME = "Product Price App";
    @Value("${sheets.id}")
    private String spreadsheetId;
    @Value("${sheets.range}")
    private String range;
    @Value("${sheets.valueInputOption}")
    private String valueInputOption;

    public SheetsService(SeleniumService seleniumService) {
        this.seleniumService = seleniumService;
    }

    @Scheduled(fixedRate = 30000)
    public void updateSheet() {

        log.info("Starting updateSheet() execution...");

        try {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            log.info("Retrieving price and title from SeleniumService...");
            List<String> pageRetrieves = seleniumService.getPriceRetriever();
            log.info("Retrieved title: {}", pageRetrieves.get(0));
            log.info("Retrieved price: {}€", pageRetrieves.get(1));

            List<List<Object>> values = new ArrayList<>();
            List<Object> row = List.of(pageRetrieves.get(0), pageRetrieves.get(1), "80", now.format(formatter));
            values.add(row);

            log.info("Appending data to Google Sheets...");
            ValueRange body = new ValueRange().setValues(values);

            loadStoredCredentialFromEnv();
            Credential credential = getCredentials(GoogleNetHttpTransport.newTrustedTransport());

            AppendValuesResponse result = new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    credential
            ).setApplicationName(APPLICATION_NAME)
                    .build()
                    .spreadsheets()
                    .values()
                    .append(spreadsheetId, range, body)
                    .setValueInputOption(valueInputOption)
                    .execute();

            log.info("{} cells updated...", result.getUpdates().getUpdatedCells());

        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 404) {
                log.error("Spreadsheet not found with ID '{}'...", spreadsheetId);
            } else {
                log.error("Error updating spreadsheet: {}...", e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error("Unexpected error in updateSheet(): {}...", e.getMessage(), e);
        }
    }
}
