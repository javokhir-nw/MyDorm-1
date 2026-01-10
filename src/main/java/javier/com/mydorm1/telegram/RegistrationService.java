package javier.com.mydorm1.telegram;

import jakarta.transaction.Transactional;
import javier.com.mydorm1.auth.model.Status;
import javier.com.mydorm1.auth.model.User;
import javier.com.mydorm1.auth.repo.UserRepository;
import javier.com.mydorm1.model.Floor;
import javier.com.mydorm1.model.Room;
import javier.com.mydorm1.repo.FloorRepository;
import javier.com.mydorm1.repo.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final FloorRepository floorRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    Map<Long,User> users = new LinkedHashMap<>();

    public EditMessageText handleRegistrationCallBackQueries(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        Long userTgId = callbackQuery.getFrom().getId();
        if (data.startsWith("registration:room: ")){
            Long roomId = Long.valueOf(data.split(" ")[1]);
            User user = users.get(userTgId);
            user.setRoom(roomRepository.findById(roomId).orElseThrow());
            user.setStatus(Status.SENT_ROOM);
            return createUpdateMessage(messageId,chatId, null,"Ismingizni kiriting (M.n: Asilbek) : ");
        }
        return null;
    }

    private EditMessageText createUpdateMessage(Integer messageId, Long chatId, InlineKeyboardMarkup markup, String text) {
        EditMessageText edt = new EditMessageText(text);
        edt.setMessageId(messageId);
        edt.setChatId(chatId);
        edt.setReplyMarkup(markup);
        return edt;
    }

    @Transactional
    public SendMessage registerUser(String token, Long chatId, Long userTelegramId, String userTelegramUsername, Integer messageId) {
        Floor floor = floorRepository.findByToken(token);
        if (userRepository.existByTelegramUsernameAndTelegramId(userTelegramId,userTelegramUsername)) {
            return createMessage(chatId,messageId,null,"Siz botdan oldin ro'yhatdan o'tgansiz");
        }
        User user;
        if (users.containsKey(userTelegramId)) {
            user = users.get(userTelegramId);
        } else {
            user = new User();
        }
        user.setTelegramId(userTelegramId.toString());
        user.setTelegramUsername(userTelegramUsername);
        user.setDormitory(floor.getDormitory());
        user.setFloor(floor);
        user.setChatId(chatId);
        user.setEnabled(Boolean.TRUE);
        user.setStatus(Status.SENT_ROOM);
        users.put(userTelegramId,user);
        return createMessage(chatId,messageId,createRoomsList(floor.getId()),"Xonangizni tanlang");
    }

    public InlineKeyboardMarkup createRoomsList(Long floorId){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<Room> rooms = roomRepository.findByFloorId(floorId);
        for (Room room: rooms){
            String number = room.getNumber();
            String name = number + "-xona";
            InlineKeyboardButton btn = new InlineKeyboardButton(name);
            btn.setCallbackData("registration:room: " + room.getId());
            keyboard.add(List.of(btn));
        }
        markup.setKeyboard(keyboard);
        return markup;
    }

    private SendMessage createMessage(Long chatId, Integer replyMessageId, ReplyKeyboard markup, String text) {
        SendMessage m = new SendMessage(chatId.toString(),text);
        m.setReplyToMessageId(replyMessageId);
        m.setReplyMarkup(markup);
        return m;
    }

    public SendMessage handleRegisterAnswers(Message message) {
        Long userTgId = message.getFrom().getId();
        Long chatId = message.getChatId();
        String text = message.getText();
        Integer messageId = message.getMessageId();
        if (!users.containsKey(userTgId)){
            return createMessage(chatId,messageId,null,"Sizda botdan foydalanish uchun ruhsat yo'q. Aloqa uchun: @javokhir_nw");
        }
        User user = users.get(userTgId);
        Status status = user.getStatus();
        if(text == null || text.isEmpty()){
            if (!message.hasContact()){
                return createMessage(chatId,messageId,null,"Yaroqli malumot kiriting");
            }
        }
        switch (status){
            case SENT_ROOM -> {
                user.setFirstName(text);
                user.setStatus(Status.SENT_FNAME);
                return createMessage(chatId,messageId,null,"Familiyangizni kiriting: ");
            }
            case SENT_FNAME -> {
                user.setLastName(text);
                user.setStatus(Status.SENT_MNAME);
                return createMessage(chatId,messageId,null,"Sharifingizni kiriting: ");
            }
            case SENT_MNAME -> {
                user.setMiddleName(text);
                user.setStatus(Status.SENT_PNUMBER);
                ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
                KeyboardButton phone = new KeyboardButton();
                phone.setText("Raqamni jo'natish");
                phone.setRequestContact(Boolean.TRUE);
                KeyboardRow row = new KeyboardRow();
                row.add(phone);
                markup.setKeyboard(List.of(row));
                return createMessage(chatId,messageId,markup,"Raqamingizni jo'nating");
            }
            case SENT_PNUMBER -> {
                user.setPhone(message.getContact().getPhoneNumber());
                user.setStatus(Status.ACTIVE);
                users.remove(userTgId);
                userRepository.save(user);
                return createMessage(chatId,messageId,null,"Muvaffaqqiyatli ro'yhatdan o'tdingiz. Iltimos chatni o'chirib yubormang!");
            }
            default -> {
                return createMessage(chatId,messageId,null,"Unreachable state. Aloqa uchun: @javokhir_nw");
            }
        }
    }
}
