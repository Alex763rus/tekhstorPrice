package com.example.tekhstorprice.model.menu.client.price;

import com.example.tekhstorprice.enums.SheetName;
import com.example.tekhstorprice.model.jpa.HistoryAction;
import com.example.tekhstorprice.model.jpa.HistoryActionRepository;
import com.example.tekhstorprice.model.jpa.User;
import com.example.tekhstorprice.model.menu.Menu;
import com.example.tekhstorprice.model.sheet.price.Price;
import com.example.tekhstorprice.model.sheet.price.PriceGroup;
import com.example.tekhstorprice.model.wpapper.SendMessageWrap;
import com.example.tekhstorprice.service.google.PriceService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.tekhstorprice.constant.Constant.*;
import static com.example.tekhstorprice.constant.Constant.Command.COMMAND_CHECK_PRICE;
import static com.example.tekhstorprice.constant.Constant.Command.COMMAND_CONTACT_MANAGER;
import static com.example.tekhstorprice.enums.State.*;
import static com.example.tekhstorprice.utils.StringUtils.prepareShield;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.stream.Collectors.toList;

@Component
@Slf4j
public class MenuCheckPrice extends Menu {

    @Autowired
    protected HistoryActionRepository historyActionRepository;

    @Autowired
    private PriceService priceService;


    private Map<User, SheetName> sheetTmp = new HashMap<>();
    private Map<User, String> groupTmp = new HashMap<>();
    private Map<User, Price> priceTmp = new HashMap<>();
    private Map<User, HistoryAction> historyActionTmp = new HashMap<>();

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
            switch (stateService.getState(user)) {
                case FREE:
                    return freelogic(user, update);
                case CLIENT_WAIT_SHEET_NAME:
                    return waitSheetNameLogic(user, update);
                case CLIENT_WAIT_GROUP_NAME:
                    return waitGroupNameLogic(user, update);
                case CLIENT_WAIT_ITEM:
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
        text.append(STAR).append("Укажите категорию товара:").append(STAR).append(NEW_LINE);
        for (val sheetName : SheetName.values()) {
            text.append("- ").append(sheetName.getTitle()).append(": ")
                    .append(prepareShield(sheetName.getCommandName())).append(NEW_LINE);
        }
        stateService.setState(user, CLIENT_WAIT_SHEET_NAME);
        return SendMessageWrap.init()
                .setChatIdLong(user.getChatId())
                .setText(text.toString()).build()
                .createSendMessageList();
    }

    private List<PartialBotApiMethod> waitSheetNameLogic(User user, Update update) {
        val sheet = SheetName.valueOfCommand(update.getMessage().getText());
        val price = priceService.getPrice(sheet);
        val groups = price.values().stream()
                .map(e -> e.getPriceGroup())
                .sorted(Comparator.comparingInt(PriceGroup::getOrder))
                .distinct()
                .collect(toList());
        val text = new StringBuilder();
        text.append(STAR).append("Укажите группу товара:").append(STAR).append(NEW_LINE);
        for (val group : groups) {
            text.append("- ").append(group.getGroupName()).append(": ")
                    .append(prepareShield(group.getCommand())).append(NEW_LINE);
        }
        sheetTmp.put(user, sheet);
        stateService.setState(user, CLIENT_WAIT_GROUP_NAME);
        return SendMessageWrap.init()
                .setChatIdLong(user.getChatId())
                .setText(text.toString()).build()
                .createSendMessageList();
    }

    private List<PartialBotApiMethod> waitGroupNameLogic(User user, Update update) {
        val group = update.getMessage().getText();
        val price = priceService.getPrice(sheetTmp.get(user));
        val items = price.values().stream()
                .filter(e -> e.getPriceGroup().getCommand().equals(group))
                .sorted(Comparator.comparingInt(Price::getOrder))
                .collect(Collectors.toList());

        val text = new StringBuilder();
        text.append(STAR).append("Укажите модель:").append(STAR).append(NEW_LINE);
        for (val item : items) {
            text.append("- ").append(item.getName()).append(": ")
                    .append(prepareShield(item.getCommand())).append(NEW_LINE);
        }
        groupTmp.put(user, group);
        stateService.setState(user, CLIENT_WAIT_ITEM);
        return SendMessageWrap.init()
                        .setChatIdLong(user.getChatId())
                        .setText(text.toString()).build()
                        .createSendMessageList();
    }

    private List<PartialBotApiMethod> waitItemLogic(User user, Update update) {
        val item = update.getMessage().getText();
        val group = groupTmp.get(user);
        val sheetName = sheetTmp.get(user);
        val prices = priceService.getPrice(sheetName);

        val price = prices.values().stream()
                .filter(e -> e.getPriceGroup().getCommand().equals(group))
                .filter(e -> e.getCommand().equals(item))
                .findFirst().orElse(null);

        priceTmp.put(user, price);

        val historyAction = new HistoryAction();
        historyAction.setChatIdFrom(user.getChatId());
        historyAction.setSheetName(sheetName.getTitle());
        historyAction.setGroupPrice(price.getPriceGroup().getGroupName());
        historyAction.setModelPrice(price.getName());
        historyAction.setActionDate(new Timestamp(System.currentTimeMillis()));
        historyAction.setContactManager(FALSE);
        historyActionRepository.save(historyAction);

        historyActionTmp.put(user, historyAction);

        val text = new StringBuilder();
        text.append(STAR).append("Товар: ").append(STAR).append(price.getPriceGroup().getGroupName()).append(" ").append(price.getName()).append(NEW_LINE)
                .append(STAR).append("Цена с гарантией 2 года: ").append(STAR).append(price.getPrice2year()).append(NEW_LINE)
                .append(STAR).append("Цена с гарантией 14 дней: ").append(STAR).append(price.getPriceDrop()).append(NEW_LINE)
                .append(STAR).append("Оптовая цена: ").append(STAR).append(price.getPriceOpt()).append(NEW_LINE)
                .append(NEW_LINE)
                .append("- консультация с менеджером: ").append(prepareShield(COMMAND_CONTACT_MANAGER)).append(NEW_LINE)
                .append("- просчитать другой товар: ").append(prepareShield(COMMAND_CHECK_PRICE));

        groupTmp.remove(user);
        sheetTmp.remove(user);
        stateService.setState(user, CLIENT_WAIT_CONSULTATION);

        return SendMessageWrap.init()
                .setChatIdLong(user.getChatId())
                .setText(text.toString())
                .build()
                .createSendMessageList();
    }

    private List<PartialBotApiMethod> waitConsultationLogic(User user, Update update) {
        val command = update.getMessage().getText();
        if (!command.equals(COMMAND_CONTACT_MANAGER)) {
            return errorMessageDefault(update);
        }
        val textManager = new StringBuilder();
        val price = priceTmp.get(user);

        val historyAction = historyActionTmp.get(user);
        historyAction.setContactManager(TRUE);
        historyActionRepository.save(historyAction);

        textManager.append("Обратился клиент: ").append(prepareShield("@" + user.getUserName())).append(NEW_LINE)
                .append(STAR).append("ФИО: ").append(STAR)
                .append(prepareShield(user.getFirstName())).append(SPACE)
                .append(prepareShield(user.getLastName())).append(NEW_LINE)
                .append(STAR).append("Товар: ").append(STAR).append(price.getPriceGroup().getGroupName()).append(" ").append(price.getName()).append(NEW_LINE)
                .append(STAR).append("Цена с гарантией 2 года: ").append(STAR).append(price.getPrice2year()).append(NEW_LINE)
                .append(STAR).append("Цена с гарантией 14 дней: ").append(STAR).append(price.getPriceDrop()).append(NEW_LINE)
                .append(STAR).append("Оптовая цена: ").append(STAR).append(price.getPriceOpt()).append(NEW_LINE);
        val textClient = new StringBuilder();
        textClient.append("Спасибо за обращение!").append(NEW_LINE)
                .append("Информация передана менеджеру").append(NEW_LINE)
                .append("В ближайшее время свяжемся с Вами и ответим на все интересующие вопросы!");

        priceTmp.remove(user);
        historyActionTmp.remove(user);
        stateService.refreshUser(user);

        return List.of(
                SendMessageWrap.init()
                        .setChatIdString(botConfig.getManagerChatId())
                        .setText(textManager.toString())
                        .build()
                        .createSendMessage(),
                SendMessageWrap.init()
                        .setChatIdLong(user.getChatId())
                        .setText(textClient.toString())
                        .build()
                        .createSendMessage()
        );
    }
}