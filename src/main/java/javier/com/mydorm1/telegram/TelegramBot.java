package javier.com.mydorm1.telegram;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class TelegramBot extends AbilityBot {

    private final TelegramBotService telegramBotService;

    public TelegramBot(
            @Value("${telegram.bot.token}") String token,
            @Value("${telegram.bot.username}") String username,
            TelegramBotService telegramBotService
    ) {
        super(token, username);
        this.telegramBotService = telegramBotService;
    }

    @Override
    public long creatorId() {
        return 8124262674L;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (update.hasCallbackQuery()) {
            sendMessages(telegramBotService.handleCallBackQuery(update.getCallbackQuery()));
        } else if (update.hasMessage() && update.getMessage().getChat().getType().equals("private")) {
            SendMessage method = telegramBotService.handleMessages(message);
            if (method != null) {
                execute(method);
            }
        }
    }

    @SneakyThrows
    private void sendMessages(TelegramResponse telegramResponse) {
        telegramResponse.getSendMessages().forEach(m -> {
            try {
                execute(m);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        });
        telegramResponse.getEditMessageText().forEach(m -> {
            try {
                execute(m);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        });
    }
}