package javier.com.mydorm1.telegram;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

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
        if (update.hasCallbackQuery()) {
            execute(telegramBotService.handleCallBackQuery(update.getCallbackQuery()));
        } else if (update.hasMessage()){
            execute(telegramBotService.handleMessages(update.getMessage()));
        } else {
            return;
        }
    }
}