package com.cyber;

import com.cyber.util.command.CommandHandler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyTelegramBot extends TelegramLongPollingBot {

    private final String securityToken;
    private final String name;
    private Map<Long, Integer> lastMessageIdMap = new ConcurrentHashMap<>();

    private CommandHandler<Message> textMessageHandler = new CommandHandler<>();
    private CommandHandler<CallbackQuery> callbackQueryHandler = new CommandHandler<>();

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
                Message msg = update.getMessage();
                if (msg.hasText()) {
                    onTextMessage(msg);
                }
                return;
            }
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveLastMessageId(Long chatId, Integer messageId) {
        lastMessageIdMap.put(chatId, messageId);
    }

    public Integer getLastMesageId(Long chatId) {
        return lastMessageIdMap.get(chatId);
    }

    public CommandHandler<Message> getTextMessageHandler() {
        return textMessageHandler;
    }

    public CommandHandler<CallbackQuery> getCallbackQueryHandler() {
        return callbackQueryHandler;
    }

    protected void onCallbackQuery(CallbackQuery callbackQuery) throws Exception {
        executeAsync(AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQuery.getId())
                .build());
        callbackQueryHandler.invoke(callbackQuery.getData(), callbackQuery);
    }

    protected void onTextMessage(Message msg) throws Exception {
        textMessageHandler.invoke(msg.getText(), msg);
    }

    public void sendImageFile(Long chatId, String filename) throws TelegramApiException {
        InputFile inputFile = new InputFile(new File("img/" + filename), filename);
        execute(SendPhoto.builder()
                .chatId(chatId)
                .photo(inputFile)
                .build());
    }

    public String formatUserName(User user) {
        String userName = user.getFirstName() + "@" + (user.getUserName() != null ? user.getUserName() : user.getId().toString());
        return userName;
    }

    public void updateMessage(Long chatId, int messageId, String text, InlineKeyboardMarkup replyMarkup) throws TelegramApiException {
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
