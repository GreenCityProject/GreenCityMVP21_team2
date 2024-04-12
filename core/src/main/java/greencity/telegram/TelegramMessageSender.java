package greencity.telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramMessageSender {
    private final String botToken;
    private final TelegramBot telegramBot;

    public TelegramMessageSender(String botToken) {
        this.botToken = botToken;
        this.telegramBot = new TelegramBot(botToken);
    }

    public void sendMessage(String chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        try {
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
