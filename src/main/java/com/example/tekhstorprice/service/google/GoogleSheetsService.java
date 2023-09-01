package com.example.tekhstorprice.service.google;

import com.example.tekhstorprice.enums.SheetName;
import com.example.tekhstorprice.exception.GoogleSheetException;
import com.example.tekhstorprice.model.sheet.settings.Sheet;
import com.example.tekhstorprice.model.sheet.settings.SheetsSettings;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.json.gson.GsonFactory;

import static com.example.tekhstorprice.constant.Constant.App.APP_NAME;

@Service
@Log4j2
public class GoogleSheetsService {

    @Autowired
    private Credential credential;

    @Autowired
    private SheetsSettings sheetsSettings;

    public List<List<Object>> getSheetData(SheetName sheetName) {
        try {
            val sheet = sheetsSettings.getSheet(sheetName);
            Sheets service = new Sheets
                    .Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), credential)
                    .setApplicationName(APP_NAME)
                    .build();
            ValueRange response = service.spreadsheets().values()
                    .get(sheet.getSpreadsheetId(), sheet.getRange())
                    .execute();
            return response.getValues();
        } catch (Exception ex) {
            log.error(ex);
            throw new GoogleSheetException(ex.getMessage());
        }
    }
}
