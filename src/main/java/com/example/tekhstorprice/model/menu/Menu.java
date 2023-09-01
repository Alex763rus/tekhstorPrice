package com.example.tekhstorprice.model.menu;

import com.example.tekhstorprice.config.BotConfig;
import com.example.tekhstorprice.model.jpa.HistoryActionRepository;
import com.example.tekhstorprice.model.sheet.settings.SheetsSettings;
import com.example.tekhstorprice.service.database.UserService;
import com.example.tekhstorprice.service.menu.ButtonService;
import com.example.tekhstorprice.service.menu.StateService;
import jakarta.persistence.MappedSuperclass;
import org.example.tgcommons.model.wrapper.SendMessageWrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

@MappedSuperclass
public abstract class Menu implements MenuActivity {

    @Autowired
    protected BotConfig botConfig;

    @Autowired
    protected StateService stateService;

    @Autowired
    protected ButtonService buttonService;

    @Autowired
    protected SheetsSettings sheetsSettings;

    @Autowired
    protected UserService userService;

    @Autowired
    protected HistoryActionRepository historyActionRepository;

    private static final String DEFAULT_TEXT_ERROR = "Ошибка! Команда не найдена";

    protected List<PartialBotApiMethod> errorMessageDefault(Update update) {
        return SendMessageWrap.init()
                .setChatIdLong(update.getMessage().getChatId())
                .setText(DEFAULT_TEXT_ERROR)
                .build().createMessageList();
    }

    protected List<PartialBotApiMethod> errorMessage(Update update, String message) {
        return SendMessageWrap.init()
                .setChatIdLong(update.getMessage().getChatId())
                .setText(message)
                .build().createMessageList();
    }

    protected PartialBotApiMethod createAdminMessage(String message) {
        return SendMessageWrap.init()
                .setChatIdString(botConfig.getAdminChatId())
                .setText(message)
                .build().createMessage();
    }
}
