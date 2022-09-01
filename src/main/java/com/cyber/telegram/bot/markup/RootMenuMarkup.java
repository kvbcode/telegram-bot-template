package com.cyber.telegram.bot.markup;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;

public class RootMenuMarkup{
    private static volatile InlineKeyboardMarkup instance = null;

    private RootMenuMarkup(){}

    public static InlineKeyboardMarkup getInstance(){
        if (instance==null){
            InlineKeyboardButton helloButton = InlineKeyboardButton.builder()
                    .text("Hello").callbackData("but:hello").build();

            InlineKeyboardButton hiButton = InlineKeyboardButton.builder()
                    .text("Hi").callbackData("but:hi").build();

            InlineKeyboardButton imageButton = InlineKeyboardButton.builder()
                    .text("Image").callbackData("but:image").build();

            InlineKeyboardButton submenuButton = InlineKeyboardButton.builder()
                    .text("Submenu").callbackData("but:submenu").build();

            instance = InlineKeyboardMarkup.builder()
                    .keyboardRow(Arrays.asList(helloButton, hiButton))
                    .keyboardRow(Arrays.asList(imageButton))
                    .keyboardRow(Arrays.asList(submenuButton))
                    .build();
        }
        return instance;
    }

}
