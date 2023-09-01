package com.example.tekhstorprice.service.menu;

import com.example.tekhstorprice.model.dictionary.security.Security;
import com.example.tekhstorprice.model.menu.*;
import com.example.tekhstorprice.model.menu.admin.MenuUpdatePrice;
import com.example.tekhstorprice.model.menu.admin.MenuViewClient;
import com.example.tekhstorprice.model.menu.client.price.MenuCheckPrice;
import com.example.tekhstorprice.service.database.UserService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.tgcommons.model.wrapper.EditMessageTextWrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.ArrayList;
import java.util.List;

import static com.example.tekhstorprice.enums.State.FREE;
import static org.example.tgcommons.constant.Constant.Command.COMMAND_START;

@Slf4j
@Service
public class MenuService {

    @Autowired
    private MenuDefault menuActivityDefault;

    @Autowired
    private StateService stateService;

    @Autowired
    private UserService userService;

    @Autowired
    private Security security;

    @Autowired
    private MenuStart menuStart;

    @Autowired
    private MenuUpdatePrice menuUpdatePrice;

    @Autowired
    private MenuCheckPrice menuCheckPrice;
    @Autowired
    private MenuViewClient menuViewClient;

    @PostConstruct
    public void init() {
        // Список всех возможных обработчиков меню:
        security.setMainMenu(List.of(menuStart, menuUpdatePrice, menuCheckPrice, menuViewClient));
    }

    public List<PartialBotApiMethod> messageProcess(Update update) {
        val user = userService.getUser(update);
        MenuActivity menuActivity = null;
        if (update.hasMessage()) {
            for (val menu : security.getMainMenu()) {
                if (menu.getMenuComand().equals(update.getMessage().getText())) {
                    if (security.checkAccess(user, menu.getMenuComand())) {
                        menuActivity = menu;
                    } else {
                        menuActivity = menuActivityDefault;
                    }
                }
            }
        }
        if (menuActivity != null) {
            stateService.setMenu(user, menuActivity);
        } else {
            menuActivity = stateService.getMenu(user);
            if (menuActivity == null) {
                log.warn("Не найдена команда с именем: " + update.getMessage().getText());
                menuActivity = menuActivityDefault;
            }
        }
        val answer = new ArrayList<PartialBotApiMethod>();
        if (update.hasCallbackQuery()) {
            val message = update.getCallbackQuery().getMessage();
            val menuName = message.getReplyMarkup().getKeyboard().stream()
                    .filter(e -> e.get(0).getCallbackData()!=null)
                    .filter(e -> e.get(0).getCallbackData().equals(update.getCallbackQuery().getData()))
                    .findFirst().get().get(0).getText();
            answer.add(EditMessageTextWrap.init()
                    .setChatIdLong(message.getChatId())
                    .setMessageId(message.getMessageId())
                    .setText("Выбрано меню: " + menuName)
                    .build().createMessage());
        }
        answer.addAll(menuActivity.menuRun(user, update));
        if (stateService.getState(user) == FREE && !menuActivity.getMenuComand().equals(menuStart.getMenuComand())) {
            answer.addAll(menuStart.menuRun(user, update));
        }
        return answer;
    }

    public List<BotCommand> getMainMenuComands() {
        val listofCommands = new ArrayList<BotCommand>();
        security.getMainMenu().stream()
                .filter(e -> e.getMenuComand().equals(COMMAND_START))
                .forEach(e -> listofCommands.add(new BotCommand(e.getMenuComand(), e.getDescription())));
        return listofCommands;
    }

    private String getChatId(Update update) {
        if (update.hasMessage()) {
            return String.valueOf(update.getMessage().getChatId());
        }
        if (update.hasCallbackQuery()) {
            return String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        }
        return null;
    }
}