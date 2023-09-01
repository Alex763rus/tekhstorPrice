package com.example.tekhstorprice.service;

import com.example.tekhstorprice.config.BotConfig;
import com.example.tekhstorprice.service.menu.MenuService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.tgcommons.model.wrapper.SendMessageWrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static org.example.tgcommons.constant.Constant.TextConstants.NEW_LINE;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private BotConfig botConfig;

    @Autowired
    private MenuService menuService;

    @PostConstruct
    public void init() {
        try {
            execute(new SetMyCommands(menuService.getMainMenuComands(), new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
        log.info("==" + "Server was starded. Version: " + botConfig.getBotVersion() + "==================================================================================================");
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotUserName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        val answers = menuService.messageProcess(update);
        for (val answer : answers) {
            try {
                if (answer instanceof BotApiMethod) {
                    if (answer instanceof SendMessage) {
                        val splitAnswers = splitAnswerOnToLongText((SendMessage) answer);
                        for (BotApiMethod ans : splitAnswers) {
                            execute(ans);
                        }
                    } else {
                        execute((BotApiMethod) answer);
                    }
                }
                if (answer instanceof SendDocument) {
                    execute((SendDocument) answer);
                }
            } catch (TelegramApiException e) {
                log.error("Ошибка во время обработки сообщения: " + e.getMessage());
            }
        }
    }

    private int MESSAGE_LEN_LIMIT = 4000;

    private List<SendMessage> splitAnswerOnToLongText(SendMessage answer) {
        if (answer.getText() == null || answer.getText().length() < MESSAGE_LEN_LIMIT) {
            return List.of(answer);
        }
        val splitAnswerOnToLongTextList = new ArrayList<SendMessage>();
        val text = answer.getText();
        val tokens = new ArrayList<String>();

        int start = 0;
        int step = MESSAGE_LEN_LIMIT;
        int finish = step;
        for (; start < text.length(); ) {
            finish = text.lastIndexOf(NEW_LINE, start + MESSAGE_LEN_LIMIT);
            if (start == finish) {
                finish = start + MESSAGE_LEN_LIMIT;
            }
            tokens.add(text.substring(start, Math.min(text.length(), finish)));
            start = finish;
        }
        for (val token : tokens) {
            splitAnswerOnToLongTextList.add(SendMessageWrap.init()
                    .setChatIdString(answer.getChatId())
                    .setText(token)
                    .build().createMessage()
            );
        }
        return splitAnswerOnToLongTextList;
    }
}
