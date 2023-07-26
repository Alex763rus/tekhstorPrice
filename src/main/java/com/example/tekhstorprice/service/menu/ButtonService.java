package com.example.tekhstorprice.service.menu;

import com.example.tekhstorprice.enums.State;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ButtonService {

    public InlineKeyboardMarkup createVerticalColumnMenuTest(final int countColumn, LinkedList<LinkedList<String>> menuDescription) {
        val inlineKeyboardMarkup = new InlineKeyboardMarkup();
        val rows = new ArrayList<List<InlineKeyboardButton>>();

        int indexMenu = 1;
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        for (val oneMenu : menuDescription) {
            val btn = new InlineKeyboardButton();
            btn.setCallbackData(oneMenu.get(0));
            btn.setText(oneMenu.get(1));
            if (oneMenu.size() > 2) {
                btn.setUrl(oneMenu.get(2));
            }
            rowInline.add(btn);
            if (indexMenu % countColumn == 0) {
                rows.add(rowInline);
                rowInline = new ArrayList<>();
            }
            ++indexMenu;
        }
        rows.add(rowInline);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup createVerticalMenu(Map<String, String> menuDescription) {
        val inlineKeyboardMarkup = new InlineKeyboardMarkup();
        val rows = new ArrayList<List<InlineKeyboardButton>>();

        for (val entry : menuDescription.entrySet()) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            val btn = new InlineKeyboardButton();
            btn.setText(entry.getValue().toString());
            btn.setCallbackData(entry.getKey().toString());
            rowInline.add(btn);
            rows.add(rowInline);
        }
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup createVerticalMenuState(Map<State, String> menuDescription) {
        val inlineKeyboardMarkup = new InlineKeyboardMarkup();
        val rows = new ArrayList<List<InlineKeyboardButton>>();
        for (Map.Entry entry : menuDescription.entrySet()) {
            val rowInline = new ArrayList<InlineKeyboardButton>();
            val btn = new InlineKeyboardButton();
            btn.setText(entry.getValue().toString());
            btn.setCallbackData(entry.getKey().toString());
            rowInline.add(btn);
            rows.add(rowInline);
        }
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }
}
