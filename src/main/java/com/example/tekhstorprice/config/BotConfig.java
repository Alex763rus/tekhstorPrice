package com.example.tekhstorprice.config;

import com.example.tekhstorprice.enums.UserRole;
import com.example.tekhstorprice.model.dictionary.security.Security;
import com.example.tekhstorprice.model.sheet.settings.Sheet;
import com.example.tekhstorprice.model.sheet.settings.SheetsSettings;
import com.example.tekhstorprice.service.google.GoogleSheetsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.tekhstorprice.constant.Constant.*;
import static com.example.tekhstorprice.constant.Constant.Command.*;
import static com.example.tekhstorprice.enums.UserRole.*;

@Configuration
@Data
@PropertySource("application.properties")
public class BotConfig {

    @Value("${bot.version}")
    String botVersion;

    @Value("${bot.username}")
    String botUserName;

    @Value("${bot.token}")
    String botToken;

    @Value("${admin.chatid}")
    String adminChatId;

    @Value("${manager.chatid}")
    String managerChatId;

    @Value("${manager.login}")
    String managerLogin;

    @Value("${service.file_info.uri}")
    String fileInfoUri;

    @Value("${service.file_storage.uri}")
    String fileStorageUri;
    @Autowired
    ObjectMapper objectMapper;

    @Value("${input.file.path}")
    String inputFilePath;

    private String getCurrentPath() {
        return System.getProperty(USER_DIR) + SHIELD;
    }

    @Bean
    public Security security() {
        val roleSecurity = new Security();

        // Настройка команд по ролям:
        val roleAccess = new HashMap<UserRole, List<String>>();
        roleAccess.put(BLOCKED, List.of(COMMAND_DEFAULT, COMMAND_START));
        roleAccess.put(CLIENT, List.of(COMMAND_DEFAULT, COMMAND_START, COMMAND_CHECK_PRICE));
        roleAccess.put(ADMIN, List.of(COMMAND_DEFAULT, COMMAND_START, COMMAND_CHECK_PRICE, COMMAND_UPDATE_PRICE, COMMAND_VIEW_CLIENT));
        roleSecurity.setRoleAccess(roleAccess);
        return roleSecurity;
    }

    private final static String CREDENTIALS_FILE_PATH = "/credentials.json";

    @Bean
    public Credential credential() throws IOException, GeneralSecurityException {
        InputStream in = GoogleSheetsService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(GsonFactory.getDefaultInstance(), new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(), clientSecrets, Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY))
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    @SneakyThrows
    @Bean
    SheetsSettings senderSettings() {
        val filePath = getCurrentPath() + SHEETS_SETTINGS;
        val senderSettings = objectMapper.readValue(new File(filePath), SheetsSettings.class);
        return senderSettings;
    }

}
