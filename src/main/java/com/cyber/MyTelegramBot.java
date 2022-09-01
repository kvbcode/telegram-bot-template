package com.cyber;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.Arrays;

public class MyTelegramBot extends TelegramLongPollingBot {

    private final String securityToken;
    private final String name;

    private int lastMenuMessageId = 0;

    public MyTelegramBot(String name) {
        super();
        this.name = name;
        this.securityToken = System.getenv("TELEGRAM_TOKEN");

        if (this.securityToken == null) {
            throw new RuntimeException("TELEGRAM_TOKEN is not set!");
        }
    }

    @Override
    public String getBotUsername() {
        return this.name;
    }

    @Override
    public String getBotToken() {
        return securityToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasCallbackQuery()) {
                onCallbackQuery(update.getCallbackQuery());
                return;
            }

            if (update.hasMessage()) {
                onMessage(update.getMessage());
                return;
            }
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }

    protected void onCallbackQuery(CallbackQuery callbackQuery) throws TelegramApiException {
        Message msg = callbackQuery.getMessage();
        Long chatId = msg.getChatId();
        String command = callbackQuery.getData();

        String text = "нажата кнопка " + command;
        System.out.println(text);

        AnswerCallbackQuery callbackResponse = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQuery.getId())
                .text(text)
                .build();
        execute(callbackResponse);

        if ("but:submenu".equals(command)) {
            InlineKeyboardButton backButton = InlineKeyboardButton.builder()
                    .text("<< Вернуться")
                    .callbackData("but:rootmenu")
                    .build();

            InlineKeyboardMarkup submenuMarkup = InlineKeyboardMarkup.builder()
                    .keyboardRow(Arrays.asList(backButton))
                    .build();

            updateMessage(chatId, lastMenuMessageId, "Демонстрация подменю", submenuMarkup);
            return;
        }

        if ("but:rootmenu".equals(command)) {
            updateMessage(chatId, lastMenuMessageId, "Нажмите на кнопки ниже, чтобы проверить реакцию на события.", rootMenuMarkup());
            return;
        }

        if ("but:image".equals(command)) {
            sendImageFile(msg.getChatId(), "image.jpg");
            return;
        }

    }

    protected void sendImageFile(Long chatId, String filename) throws TelegramApiException {
        InputFile inputFile = new InputFile(new File("img/" + filename), filename);

        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(chatId)
                .photo(inputFile)
                .build();

        execute(sendPhoto);
    }

    protected void onMessage(Message msg) throws TelegramApiException {
        System.out.println("Incoming message from " + msg.getFrom().toString());

        if (msg.hasText()) {
            String msgText = msg.getText();

            if ("/start".equals(msgText)) {
                onStart(msg);
                return;
            }

            if ("/menu".equals(msgText)) {
                SendMessage message = new SendMessage();
                message.setChatId(msg.getChatId());
                message.setText("Нажмите на кнопки ниже, чтобы проверить реакцию на события.");
                message.setReplyMarkup(rootMenuMarkup());

                Message result = execute(message);
                System.out.println("menu message last id: " + result.getMessageId());
                lastMenuMessageId = result.getMessageId();
                return;
            }

            String userName = formatUserName(msg.getFrom());
            System.out.println(userName + ": " + msg.getText());

            execute(SendMessage.builder()
                    .chatId(msg.getChatId())
                    .text(msg.getText())
                    .build()
            );
        }
    }

    protected InlineKeyboardMarkup rootMenuMarkup() {
        InlineKeyboardButton helloButton = InlineKeyboardButton.builder()
                .text("Hello").callbackData("but:hello").build();

        InlineKeyboardButton hiButton = InlineKeyboardButton.builder()
                .text("Hi").callbackData("but:hi").build();

        InlineKeyboardButton imageButton = InlineKeyboardButton.builder()
                .text("Image").callbackData("but:image").build();

        InlineKeyboardButton submenuButton = InlineKeyboardButton.builder()
                .text("Submenu").callbackData("but:submenu").build();

        InlineKeyboardMarkup keyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboardRow(Arrays.asList(helloButton, hiButton))
                .keyboardRow(Arrays.asList(imageButton))
                .keyboardRow(Arrays.asList(submenuButton))
                .build();

        return keyboardMarkup;
    }

    protected void onStart(Message msg) throws TelegramApiException {
        String userName = formatUserName(msg.getFrom());
        String onStartText = "Привет " + userName + ". Добро пожаловать к MyTelegramBot. Этот пример бота будет повторять сообщение пользователю. Введите /menu для вызова меню.";

        execute(SendMessage.builder()
                .chatId(msg.getChatId())
                .text(onStartText)
                .build()
        );
    }

    protected String formatUserName(User user) {
        String userName = user.getFirstName() + "@" + (user.getUserName() != null ? user.getUserName() : user.getId().toString());
        return userName;
    }

    protected void updateMessage(Long chatId, int messageId, String text, InlineKeyboardMarkup replyMarkup) throws TelegramApiException {
        if (text != null) {
            EditMessageText.EditMessageTextBuilder editMessageTextBuilder = EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(text);
            if (replyMarkup != null) editMessageTextBuilder.replyMarkup(replyMarkup);

            executeAsync(editMessageTextBuilder.build());
        } else if (replyMarkup != null) {
            EditMessageReplyMarkup editMessageMarkup = EditMessageReplyMarkup.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .replyMarkup(replyMarkup)
                    .build();
            executeAsync(editMessageMarkup);
        }

    }

}
