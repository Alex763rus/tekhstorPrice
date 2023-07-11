package com.example.tekhstorprice.model.menu;

import com.example.tekhstorprice.enums.SheetName;
import com.example.tekhstorprice.model.jpa.User;
import com.example.tekhstorprice.model.wpapper.SendMessageWrap;
import com.example.tekhstorprice.service.google.PriceService;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

import static com.example.tekhstorprice.constant.Constant.Command.*;
import static com.example.tekhstorprice.constant.Constant.NEW_LINE;
import static com.example.tekhstorprice.enums.State.CLIENT_WAIT_SHEET_NAME;
import static com.example.tekhstorprice.utils.StringUtils.prepareShield;

@Component
@Slf4j
public class MenuStart extends Menu {

    @Override
    public String getMenuComand() {
        return COMMAND_START;
    }

    @Override
    public List<PartialBotApiMethod> menuRun(User user, Update update) {
        String messageText = "";
        switch (user.getUserRole()) {
            case BLOCKED:
                messageText = "Доступ запрещен";
                break;
            case CLIENT:
                messageText = getClientStartMenuText(user);
                break;
            case ADMIN:
                messageText = getAdminStartMenuText(user);
                break;
        }
        return Arrays.asList(
                SendMessageWrap.init()
                        .setChatIdLong(user.getChatId())
                        .setText(EmojiParser.parseToUnicode(messageText))
                        .build().createSendMessage());
    }

    private String getClientStartMenuText(User user) {
        val menu = new StringBuilder();
        menu.append("Здравствуйте, ").append(user.getFirstName()).append("!").append(NEW_LINE)
                .append("Вас приветствует автоматизированный представитель компании Техстор").append(NEW_LINE)
                .append("*Главное меню:*").append(NEW_LINE)
                .append("- актуальная цена на товары: ").append(prepareShield(COMMAND_CHECK_PRICE));
        return menu.toString();
    }

    private String getAdminStartMenuText(User user) {
        val menu = new StringBuilder(getClientStartMenuText(user));
        menu.append(NEW_LINE).append(NEW_LINE)
                .append("*Меню администратора:*").append(NEW_LINE)
                .append("- обновить справочники: ").append(prepareShield(COMMAND_UPDATE_PRICE)).append(NEW_LINE)
                .append("- новые запросы: ").append(prepareShield(COMMAND_VIEW_CLIENT));
        return menu.toString();
    }

    @Override
    public String getDescription() {
        return " Начало работы";
    }
}
