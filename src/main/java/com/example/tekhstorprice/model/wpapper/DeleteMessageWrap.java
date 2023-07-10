package com.example.tekhstorprice.model.wpapper;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.val;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

@Getter
@SuperBuilder(setterPrefix = "set", builderMethodName = "init", toBuilder = true)
public class DeleteMessageWrap {
    private Integer messageId;
    private String chatIdString;
    private Long chatIdLong;

    public DeleteMessage createMessage() {
        val deleteMessage = new DeleteMessage();
        deleteMessage.setMessageId(messageId);
        val chatId = chatIdString == null ? String.valueOf(chatIdLong) : chatIdString;
        deleteMessage.setChatId(chatId);
        return deleteMessage;
    }
}
