package com.example.tekhstorprice.model.menu.admin;

import com.example.tekhstorprice.enums.ExportStatus;
import com.example.tekhstorprice.model.jpa.HistoryAction;
import com.example.tekhstorprice.model.jpa.User;
import com.example.tekhstorprice.model.menu.Menu;
import com.example.tekhstorprice.model.sheet.price.Price;
import com.example.tekhstorprice.model.wpapper.SendMessageWrap;
import com.example.tekhstorprice.service.google.PriceService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.example.tekhstorprice.constant.Constant.*;
import static com.example.tekhstorprice.constant.Constant.Command.COMMAND_UPDATE_PRICE;
import static com.example.tekhstorprice.constant.Constant.Command.COMMAND_VIEW_CLIENT;
import static com.example.tekhstorprice.enums.ExportStatus.EXPORTED_ACTION;
import static com.example.tekhstorprice.enums.ExportStatus.NEW_ACTION;
import static com.example.tekhstorprice.enums.State.FREE;
import static com.example.tekhstorprice.utils.NumberConverter.formatPrice;
import static com.example.tekhstorprice.utils.StringUtils.prepareShield;

@Component
@Slf4j
public class MenuViewClient extends Menu {

    @Override
    public String getMenuComand() {
        return COMMAND_VIEW_CLIENT;
    }

    @Override
    public String getDescription() {
        return "Показать новые запросы";
    }

    @Override
    public List<PartialBotApiMethod> menuRun(User user, Update update) {
        val historyActions = historyActionRepository.findByExportStatus(NEW_ACTION);
        if (historyActions.size() == 0) {
            return SendMessageWrap.init()
                    .setChatIdLong(user.getChatId())
                    .setText("Новые записи отсутствуют")
                    .build().createSendMessageList();
        }
        val answer = new ArrayList<PartialBotApiMethod>();
        for (int i = 0; i < historyActions.size(); ++i) {
            answer.add(getHistoryActionMessage(user, update, historyActions.get(i)));
            historyActions.get(i).setExportStatus(EXPORTED_ACTION);
        }
        historyActionRepository.saveAll(historyActions);
        stateService.setState(user, FREE);
        return answer;
    }

    private PartialBotApiMethod getHistoryActionMessage(User user, Update update, HistoryAction historyAction) {
        val textManager = new StringBuilder();
        val userHistoryAction = userService.findUserByChatId(historyAction.getChatIdFrom());
        textManager.append(STAR).append("ID обращения: ").append(STAR).append(historyAction.getHistoryActionId()).append(NEW_LINE)
                .append(STAR).append("Обратился клиент: ").append(STAR).append(prepareShield("@" + userHistoryAction.getUserName())).append(NEW_LINE)
                .append(STAR).append("ФИО: ").append(STAR)
                .append(prepareShield(user.getFirstName())).append(SPACE)
                .append(prepareShield(user.getLastName())).append(NEW_LINE)
                .append(STAR).append("Товар: ").append(STAR).append(NEW_LINE)
                .append(historyAction.getGroupPrice()).append(" ").append(historyAction.getModelPrice()).append(NEW_LINE)
                .append(STAR).append("Цена с гарантией 2 года: ").append(STAR).append(formatPrice(historyAction.getPrice2year())).append(NEW_LINE)
                .append(STAR).append("Цена с гарантией 14 дней: ").append(STAR).append(formatPrice(historyAction.getPriceDrop())).append(NEW_LINE)
                .append(STAR).append("Оптовая цена: ").append(STAR).append(formatPrice(historyAction.getPriceOpt())).append(NEW_LINE);

        return SendMessageWrap.init()
                .setChatIdLong(user.getChatId())
                .setText(textManager.toString())
                .build()
                .createSendMessage();
    }
}
