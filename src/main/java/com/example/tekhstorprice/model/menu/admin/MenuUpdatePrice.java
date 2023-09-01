package com.example.tekhstorprice.model.menu.admin;

import com.example.tekhstorprice.model.jpa.User;
import com.example.tekhstorprice.model.menu.Menu;
import com.example.tekhstorprice.service.google.PriceService;
import lombok.extern.slf4j.Slf4j;
import org.example.tgcommons.model.wrapper.SendMessageWrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static com.example.tekhstorprice.constant.Constant.Command.COMMAND_UPDATE_PRICE;
import static com.example.tekhstorprice.enums.State.FREE;

@Component
@Slf4j
public class MenuUpdatePrice extends Menu {

    @Autowired
    private PriceService priceService;

    @Override
    public String getMenuComand() {
        return COMMAND_UPDATE_PRICE;
    }

    @Override
    public String getDescription() {
        return "Обновить справочники";
    }

    @Override
    public List<PartialBotApiMethod> menuRun(User user, Update update) {
        priceService.updatePrice();
        stateService.setState(user, FREE);
        return SendMessageWrap.init()
                .setChatIdLong(user.getChatId())
                .setText(priceService.getPriceInfo())
                .build().createMessageList();
    }
}
