package com.example.tekhstorprice.model.menu.client.price;

import com.example.tekhstorprice.enums.SheetName;
import com.example.tekhstorprice.model.jpa.HistoryAction;
import com.example.tekhstorprice.model.jpa.User;
import com.example.tekhstorprice.model.menu.Menu;
import com.example.tekhstorprice.model.sheet.price.Price;
import com.example.tekhstorprice.model.sheet.price.PriceGroup;
import com.example.tekhstorprice.service.google.PriceService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.tgcommons.model.wrapper.SendMessageWrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.tekhstorprice.constant.Constant.Command.*;
import static com.example.tekhstorprice.enums.ExportStatus.NEW_ACTION;
import static com.example.tekhstorprice.enums.State.*;
import static java.util.stream.Collectors.toList;
import static org.example.tgcommons.constant.Constant.TextConstants.NEW_LINE;
import static org.example.tgcommons.constant.Constant.TextConstants.STAR;
import static org.example.tgcommons.utils.NumberConverter.formatDouble;
import static org.example.tgcommons.utils.StringUtils.prepareShield;

@Component
@Slf4j
public class MenuCheckPrice extends Menu {

    @Autowired
    private PriceService priceService;

    private Map<User, SheetName> sheetTmp = new HashMap<>();
    private Map<User, String> groupTmp = new HashMap<>();

    @Override
    public String getMenuComand() {
        return COMMAND_CHECK_PRICE;
    }

    @Override
    public String getDescription() {
        return "Проверить цену";
    }

    @Override
    public List<PartialBotApiMethod> menuRun(User user, Update update) {
        try {
            if (update.hasCallbackQuery()) {
                val callbackData = update.getCallbackQuery().getData();
                if (callbackData.equals(COMMAND_CHECK_PRICE)) {
                    return freelogic(user, update);
                }
                if (callbackData.equals(COMMAND_BACK)) {
                    return waitGroupNameLogic(user, update, groupTmp.get(user));
                }
            }
            switch (stateService.getState(user)) {
                case FREE:
                    return freelogic(user, update);
                case CLIENT_WAIT_SHEET_NAME:
                    val message = update.getMessage().getText();
                    if (message.equals(COMMAND_BACK)) {
                        userService.refreshUser(user);
                        return Arrays.asList();
                    }
                    val sheetName = SheetName.valueOfCommand(message);
                    return waitSheetNameLogic(user, update, sheetName);
                case CLIENT_WAIT_GROUP_NAME:
                    val group = update.getMessage().getText();
                    if (group.equals(COMMAND_BACK)) {
                        return freelogic(user, update);
                    }
                    return waitGroupNameLogic(user, update, group);
                case CLIENT_WAIT_ITEM:
                    val item = update.getMessage().getText();
                    if (item.equals(COMMAND_BACK)) {
                        return waitSheetNameLogic(user, update, sheetTmp.get(user));
                    }
                    return waitItemLogic(user, update);
                case CLIENT_WAIT_CONSULTATION:
                    return waitConsultationLogic(user, update);
            }
            return errorMessageDefault(update);
        } catch (Exception ex) {
            log.error(ex.toString());
            return errorMessage(update, ex.toString());
        }
    }

    private List<PartialBotApiMethod> freelogic(User user, Update update) {
        val text = new StringBuilder();
        text.append(STAR).append("Выберите категорию товара:").append(STAR).append(NEW_LINE);
        for (val sheetName : SheetName.values()) {
            text.append("- ").append(sheetName.getTitle()).append(": ")
                    .append(prepareShield(sheetName.getCommandName())).append(NEW_LINE);
        }
        text.append(NEW_LINE).append("- Назад: ").append(COMMAND_BACK).append(NEW_LINE);

        stateService.setState(user, CLIENT_WAIT_SHEET_NAME);
        return SendMessageWrap.init()
                .setChatIdLong(user.getChatId())
                .setText(text.toString()).build()
                .createMessageList();
    }

    private List<PartialBotApiMethod> waitSheetNameLogic(User user, Update update, SheetName sheetName) {
        val price = priceService.getPrice(sheetName);
        if (price == null) {
            return errorMessage(update, "Ошибка! Выбранная категорию не найдена." + NEW_LINE + "Повторите ввод");
        }
        val groups = price.values().stream()
                .map(e -> e.getPriceGroup())
                .sorted(Comparator.comparingInt(PriceGroup::getOrder))
                .distinct()
                .collect(toList());
        val text = new StringBuilder();
        text.append(STAR).append("Выберите группу товара:").append(STAR).append(NEW_LINE);
        for (val group : groups) {
            text.append("- ").append(group.getGroupName()).append(": ")
                    .append(prepareShield(group.getGroupCommand())).append(NEW_LINE);
        }
        text.append(NEW_LINE).append("- Назад: ").append(COMMAND_BACK).append(NEW_LINE);

        sheetTmp.put(user, sheetName);
        stateService.setState(user, CLIENT_WAIT_GROUP_NAME);
        return SendMessageWrap.init()
                .setChatIdLong(user.getChatId())
                .setText(text.toString()).build()
                .createMessageList();
    }

    private List<PartialBotApiMethod> waitGroupNameLogic(User user, Update update, String group) {
        val price = priceService.getPrice(sheetTmp.get(user));
        val items = price.values().stream()
                .filter(e -> e.getPriceGroup().getGroupCommand().equals(group))
                .sorted(Comparator.comparingInt(Price::getOrder))
                .collect(Collectors.toList());
        if (items.size() == 0) {
            return errorMessage(update, "Ошибка! Выбранная группа не найдена." + NEW_LINE + "Повторите ввод");
        }
        val text = new StringBuilder();
        text.append(STAR).append("Выберите модель:").append(STAR).append(NEW_LINE);
        for (val item : items) {
            text.append("- ").append(item.getName()).append(": ")
                    .append(prepareShield(item.getPriceCommand())).append(NEW_LINE);
        }
        text.append(NEW_LINE).append("- Назад: ").append(COMMAND_BACK).append(NEW_LINE);

        groupTmp.put(user, group);
        stateService.setState(user, CLIENT_WAIT_ITEM);
        return SendMessageWrap.init()
                .setChatIdLong(user.getChatId())
                .setText(text.toString()).build()
                .createMessageList();
    }

    private List<PartialBotApiMethod> waitItemLogic(User user, Update update) {
        val item = update.getMessage().getText();
        val group = groupTmp.get(user);
        val sheetName = sheetTmp.get(user);
        val prices = priceService.getPrice(sheetName);

        val price = prices.values().stream()
                .filter(e -> e.getPriceGroup().getGroupCommand().equals(group))
                .filter(e -> e.getPriceCommand().equals(item))
                .findFirst().orElse(null);

        if (price == null) {
            return errorMessage(update, "Ошибка! Выбранная модель не найдена." + NEW_LINE + "Повторите ввод");
        }

        val historyAction = new HistoryAction();
        historyAction.setChatIdFrom(user.getChatId());
        historyAction.setSheetName(sheetName.getTitle());
        historyAction.setGroupPrice(price.getPriceGroup().getGroupName());
        historyAction.setModelPrice(price.getName());
        historyAction.setActionDate(new Timestamp(System.currentTimeMillis()));
        historyAction.setExportStatus(NEW_ACTION);
        historyAction.setPrice2year(price.getPrice2year());
        historyAction.setPriceOpt(price.getPriceOpt());
        historyAction.setPriceDrop(price.getPriceDrop());

        historyActionRepository.save(historyAction);

        val textClient = new StringBuilder();
        textClient.append(STAR).append("Товар: ").append(STAR).append(NEW_LINE)
                .append(price.getPriceGroup().getGroupName()).append(" ").append(price.getName()).append(NEW_LINE)
                .append(STAR).append("Цена с гарантией 2 года: ").append(STAR).append(formatDouble(price.getPrice2year())).append(NEW_LINE)
                .append(STAR).append("Цена с гарантией 14 дней: ").append(STAR).append(formatDouble(price.getPriceDrop())).append(NEW_LINE)
                .append(STAR).append("Оптовая цена: ").append(STAR).append(formatDouble(price.getPriceOpt())).append(NEW_LINE)
                .append(NEW_LINE)
        ;

        val menuDescription = new LinkedList<LinkedList<String>>();
        val btn1 = new LinkedList<String>();
        btn1.add("key1");
        btn1.add("Менеджер");
        btn1.add(botConfig.getManagerLogin());
        val btn2 = new LinkedList<String>();
        btn2.add(COMMAND_CHECK_PRICE);
        btn2.add("Узнать цену");
        val btn3 = new LinkedList<String>();
        btn3.add(COMMAND_BACK);
        btn3.add("Назад");

        menuDescription.add(btn1);
        menuDescription.add(btn2);
        menuDescription.add(btn3);

        val btns = buttonService.createVerticalColumnMenuTest(1, menuDescription);

        return List.of(SendMessageWrap.init()
                        .setChatIdLong(user.getChatId())
                        .setText(textClient.toString())
                        .setInlineKeyboardMarkup(btns)
                        .build()
                        .createMessage(),
                SendMessageWrap.init()
                        .setChatIdString(botConfig.getManagerChatId())
                        .setText("*Внимание*, новый запрос от клиента, id: " + historyAction.getHistoryActionId())
                        .build()
                        .createMessage());
    }

    private List<PartialBotApiMethod> waitConsultationLogic(User user, Update update) {
        val command = update.getMessage().getText();
        if (!command.equals(COMMAND_CONTACT_MANAGER)) {
            return errorMessageDefault(update);
        }
        return freelogic(user, update);
    }
}
