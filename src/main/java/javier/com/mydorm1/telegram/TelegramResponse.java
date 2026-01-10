package javier.com.mydorm1.telegram;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TelegramResponse {
    private List<SendMessage> sendMessages = new ArrayList<>();
    private List<EditMessageText> editMessageText = new ArrayList<>();
}
