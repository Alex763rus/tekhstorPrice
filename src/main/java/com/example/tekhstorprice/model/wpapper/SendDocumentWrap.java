package com.example.tekhstorprice.model.wpapper;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.val;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static com.example.tekhstorprice.constant.Constant.PARSE_MODE;

@Getter
@SuperBuilder(setterPrefix = "set", builderMethodName = "init", toBuilder = true)
public class SendDocumentWrap {

    private String chatIdString;
    private Long chatIdLong;
    private InputFile document;
    private InlineKeyboardMarkup inlineKeyboardMarkup;

    public SendDocument createMessage() {
        val sendDocument = new SendDocument();
        val chatId = chatIdString == null ? String.valueOf(chatIdLong) : chatIdString;
        sendDocument.setChatId(chatId);
        sendDocument.setReplyMarkup(inlineKeyboardMarkup);
        sendDocument.setParseMode(PARSE_MODE);
        sendDocument.setDocument(document);
        return sendDocument;
    }
}
