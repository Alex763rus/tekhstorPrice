package com.example.tekhstorprice.model.wpapper;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.val;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;

@Getter
@SuperBuilder(setterPrefix = "set", builderMethodName = "init", toBuilder = true)
public class ForwardMessageWrap {

    private String chatIdString;
    private Long chatIdLong;
    private String chatIdFromString;
    private Long chatIdFromLong;
    private Integer messageId;

    public ForwardMessage createMessage() {
        val forwardMessage = new ForwardMessage();
        val chatId = chatIdString == null ? String.valueOf(chatIdLong) : chatIdString;
        val chatIdFrom = chatIdFromString == null ? String.valueOf(chatIdFromLong) : chatIdFromString;
        forwardMessage.setChatId(chatId);
        forwardMessage.setFromChatId(chatIdFrom);
        forwardMessage.setMessageId(messageId);
        return forwardMessage;
    }
}
