package com.example.tekhstorprice.model.menu;

import com.example.tekhstorprice.model.jpa.User;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.tgcommons.model.wrapper.SendMessageWrap;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static com.example.tekhstorprice.constant.Constant.Command.*;
import static org.example.tgcommons.constant.Constant.Command.COMMAND_START;
import static org.example.tgcommons.constant.Constant.TextConstants.NEW_LINE;
import static org.example.tgcommons.utils.StringUtils.prepareShield;

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
        return SendMessageWrap.init()
                .setChatIdLong(user.getChatId())
                .setText(EmojiParser.parseToUnicode(messageText))
                .build().createMessageList();
    }

    private String getClientStartMenuText(User user) {
        val menu = new StringBuilder();
        menu.append("Здравствуйте, ").append(user.getFirstName()).append("!").append(NEW_LINE)
                .append("Вас приветствует автоматизированный консультант Бот магазина Техстор").append(NEW_LINE)
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
