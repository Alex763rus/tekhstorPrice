package com.example.tekhstorprice.model.menu;

import com.example.tekhstorprice.config.BotConfig;
import com.example.tekhstorprice.model.jpa.HistoryActionRepository;
import com.example.tekhstorprice.model.sheet.settings.SheetsSettings;
import com.example.tekhstorprice.service.database.UserService;
import com.example.tekhstorprice.service.excel.ExcelGenerateService;
import com.example.tekhstorprice.service.google.GoogleSheetsService;
import com.example.tekhstorprice.service.menu.ButtonService;
import com.example.tekhstorprice.service.menu.StateService;
import jakarta.persistence.MappedSuperclass;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import com.example.tekhstorprice.model.wpapper.SendMessageWrap;

import java.util.Arrays;
import java.util.List;

@MappedSuperclass
public abstract class Menu implements MenuActivity {

    @Autowired
    protected BotConfig botConfig;

    @Autowired
    protected StateService stateService;

    @Autowired
    protected ExcelGenerateService excelGenerateService;

    @Autowired
    protected ButtonService buttonService;

    @Autowired
    protected SheetsSettings sheetsSettings;

    @Autowired
    protected UserService userService;

    private static final String DEFAULT_TEXT_ERROR = "Ошибка! Команда не найдена";

    protected List<PartialBotApiMethod> errorMessageDefault(Update update) {
        return Arrays.asList(SendMessageWrap.init()
                .setChatIdLong(update.getMessage().getChatId())
                .setText(DEFAULT_TEXT_ERROR)
                .build().createSendMessage());
    }

    protected List<PartialBotApiMethod> errorMessage(Update update, String message) {
        return Arrays.asList(SendMessageWrap.init()
                .setChatIdLong(update.getMessage().getChatId())
                .setText(message)
                .build().createSendMessage());
    }

    protected PartialBotApiMethod createAdminMessage(String message) {
        return SendMessageWrap.init()
                .setChatIdString(botConfig.getAdminChatId())
                .setText(message)
                .build().createSendMessage();
    }
}
