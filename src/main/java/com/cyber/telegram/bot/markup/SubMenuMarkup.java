package com.cyber.telegram.bot.markup;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;

public class SubMenuMarkup {
    private static volatile InlineKeyboardMarkup instance = null;

    private SubMenuMarkup(){}

    public static InlineKeyboardMarkup getInstance(){
        if (instance==null){
            InlineKeyboardButton backButton = InlineKeyboardButton.builder()
                    .text("<< Вернуться")
                    .callbackData("but:rootmenu")
                    .build();

            instance = InlineKeyboardMarkup.builder()
                    .keyboardRow(Arrays.asList(backButton))
                    .build();
        }
        return instance;
    }

}
