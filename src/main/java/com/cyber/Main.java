package com.cyber;

import com.cyber.util.command.CommandHandler;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {

    public static void main(String[] args) throws Exception {

        MyTelegramBot myTelegramBot = new MyTelegramBot("MyTelegramBot");

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(myTelegramBot);

        CommandHandler<Message> textMessageHandler = myTelegramBot.getTextMessageHandler();

        textMessageHandler.setDefaultCommand(msg -> {
            String userName = myTelegramBot.formatUserName(msg.getFrom());
            System.out.println(userName + ": " + msg.getText());

            myTelegramBot.execute(SendMessage.builder()
                    .chatId(msg.getChatId())
                    .text(msg.getText())
                    .build());
        });

        textMessageHandler.register("/start", msg -> {
            String userName = myTelegramBot.formatUserName(msg.getFrom());
            String onStartText = "Привет " + userName + ". Добро пожаловать к MyTelegramBot. Этот пример бота будет повторять сообщение пользователю. Введите /menu для вызова меню.";

            myTelegramBot.execute(SendMessage.builder()
                    .chatId(msg.getChatId())
                    .text(onStartText)
                    .build());
        });

        textMessageHandler.register("/menu", msg -> {
            Message result = myTelegramBot.execute(SendMessage.builder()
                    .chatId(msg.getChatId())
                    .text("Нажмите на кнопки ниже, чтобы проверить реакцию на события.")
                    .replyMarkup(myTelegramBot.rootMenuMarkup())
                    .build());
            myTelegramBot.saveLastMessageId(msg.getChatId(), result.getMessageId());
        });

    }

}
