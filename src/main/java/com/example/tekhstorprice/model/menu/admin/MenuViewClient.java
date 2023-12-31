package com.example.tekhstorprice.model.menu.admin;

import com.example.tekhstorprice.model.jpa.HistoryAction;
import com.example.tekhstorprice.model.jpa.User;
import com.example.tekhstorprice.model.menu.Menu;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.tgcommons.model.wrapper.SendMessageWrap;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

import static com.example.tekhstorprice.constant.Constant.*;
import static com.example.tekhstorprice.constant.Constant.Command.COMMAND_VIEW_CLIENT;
import static com.example.tekhstorprice.enums.ExportStatus.EXPORTED_ACTION;
import static com.example.tekhstorprice.enums.ExportStatus.NEW_ACTION;
import static com.example.tekhstorprice.enums.State.FREE;
import static org.example.tgcommons.constant.Constant.TextConstants.*;
import static org.example.tgcommons.utils.NumberConverter.formatDouble;
import static org.example.tgcommons.utils.StringUtils.prepareShield;

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
                    .build().createMessageList();
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
                .append(STAR).append("Цена с гарантией 2 года: ").append(STAR).append(formatDouble(historyAction.getPrice2year())).append(NEW_LINE)
                .append(STAR).append("Цена с гарантией 14 дней: ").append(STAR).append(formatDouble(historyAction.getPriceDrop())).append(NEW_LINE)
                .append(STAR).append("Оптовая цена: ").append(STAR).append(formatDouble(historyAction.getPriceOpt())).append(NEW_LINE);

        return SendMessageWrap.init()
                .setChatIdLong(user.getChatId())
                .setText(textManager.toString())
                .build()
                .createMessage();
    }
}
