package com.product.price.oauth;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Collections;

public class OAuth {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "Product Price App";


    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        String base64Credentials = System.getenv("GOOGLE_CREDENTIALS_BASE64");

        if (base64Credentials == null) {
            throw new IllegalStateException("Environment variable GOOGLE_CREDENTIALS_BASE64 not set");
        }

        byte[] decoded = Base64.getDecoder().decode(base64Credentials);
        InputStream credentialsStream = new ByteArrayInputStream(decoded);

        GoogleCredential credential = GoogleCredential.fromStream(credentialsStream)
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
