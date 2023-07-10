package com.example.tekhstorprice.model.wpapper;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.val;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

import static com.example.tekhstorprice.constant.Constant.PARSE_MODE;

@Getter
@SuperBuilder(setterPrefix = "set", builderMethodName = "init", toBuilder = true)
public class SendMessageWrap {

    private String chatIdString;
    private Long chatIdLong;
    private String text;
    private InlineKeyboardMarkup inlineKeyboardMarkup;

    public SendMessage createSendMessage() {
        val sendMessage = new SendMessage();
        val chatId = chatIdString == null ? String.valueOf(chatIdLong) : chatIdString;
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setParseMode(PARSE_MODE);
        return sendMessage;
    }

    public java.util.List<PartialBotApiMethod> createSendMessageList() {
        return List.of(createSendMessage());
    }

}
