package javier.com.mydorm1.telegram;

import jakarta.transaction.Transactional;
import javier.com.mydorm1.auth.model.User;
import javier.com.mydorm1.auth.repo.UserRepository;
import javier.com.mydorm1.model.Attendance;
import javier.com.mydorm1.model.Room;
import javier.com.mydorm1.repo.AttendanceRepository;
import javier.com.mydorm1.repo.Floor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodSerializable;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TelegramBotService {

    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final Map<Long, Set<Long>> absentUsersMap = new LinkedHashMap<>();

    @Transactional
    public EditMessageText handleCallBackQuery(CallbackQuery callbackQuery) {
        String url = callbackQuery.getData();

        Message message = callbackQuery.getMessage();
        Long chatId = message.getChatId();
        String userName = callbackQuery.getFrom().getUserName();
        Long userTelegramId = callbackQuery.getFrom().getId();

        User user = getUserByTelegramData(userName, userTelegramId);

        if (url.startsWith("toggle: ")) {
            addOrRemoveUsersFromAbsentUsers(Long.valueOf(url.split(" ")[1]), chatId);
            return updateMessage(
                    chatId,
                    message.getMessageId(),
                    startDavomat(user, chatId),
                    "Davomat"
            );
        } else if (url.equals("/end_duty")) {
            finishAttendance(chatId, user);
            return updateMessage(
                    chatId,
                    message.getMessageId(),
                    null,
                    "Davomat muvaffaqqiyatli saqlandi !"
            );
        }
        return null;
    }

    public User getUserByTelegramData(String username, Long telegramId) {
        return userRepository.findByTelegramUsernameOrTelegramId(username, telegramId);
    }

    public void finishAttendance(Long chatId, User user) {
        if (!attendanceRepository.hasCreatedTodayAttendance(user.getFloor().getId(), new Date())) {
            Set<Long> absents = absentUsersMap.get(chatId);
            String result = absents.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            Attendance attendance = new Attendance();
            attendance.setAbsentUserIds(result);
            attendance.setCreatedDate(new Date());
            attendance.setFloor(user.getFloor());
            attendanceRepository.save(attendance);
            absentUsersMap.remove(chatId);
        }
    }

    private EditMessageText updateMessage(Long chatId, Integer messageId, InlineKeyboardMarkup inlineKeyboardMarkup, String text) {
        EditMessageText emt = new EditMessageText();
        emt.setChatId(chatId);
        emt.setText(text);
        emt.setMessageId(messageId);
        emt.setReplyMarkup(inlineKeyboardMarkup);
        return emt;
    }

    private void addOrRemoveUsersFromAbsentUsers(Long aLong, Long chatId) {
        if (absentUsersMap.containsKey(chatId)) {
            Set<Long> absents = absentUsersMap.get(chatId);
            if (absents.contains(aLong)) {
                absents.remove(aLong);
            } else {
                absents.add(aLong);
            }
        }
    }

    public SendMessage handleMessages(Message message) {
        String text = message.getText();
        if (hasToken(text)) {
            String token = text.split(" ")[1];
            handleToken(token);
        } else {
            if (isRegisteredUser(message.getFrom().getUserName(), message.getFrom().getId())) {
                return handleMessage(message);
            }
        }
        return createMessage(message.getChatId(), message.getMessageId(), null, "Sizda botdan foydalanish uchun huquq yo'q bog'lanish uchun @javokhir_nw ga murojaat qiling");
    }

    private SendMessage createMessage(Long chatId, Integer messageId, ReplyKeyboard markup, String text) {
        SendMessage edt = new SendMessage();
        edt.setText(text);
        edt.setReplyToMessageId(messageId);
        edt.setReplyMarkup(markup);
        edt.setChatId(chatId);
        return edt;
    }

    private SendMessage handleMessage(Message message) {
        String text = message.getText();
        String userName = message.getFrom().getUserName();
        Long userId = message.getFrom().getId();
        User user = getUserByTelegramData(userName, userId);
        switch (text) {
            case "/start" -> {
                return handleStart(message);
            }
            case "Davomat" -> {
                Long chatId = message.getChatId();
                Integer messageId = message.getMessageId();
                if (attendanceRepository.hasCreatedTodayAttendance(user.getFloor().getId(), new Date())) {
                    return createMessage(chatId, messageId, null, "Bugungi davomat yaratilgan!");
                }
                return createMessage(chatId, messageId, startDavomat(user, chatId), "Davomat");
            }
            default -> {
                return null;
            }
        }
    }


    public InlineKeyboardMarkup startDavomat(User user, Long chatId) {
        Floor floor = user.getFloor();
        List<User> users = userRepository.findAllUsersFetchRoomByFloorId(floor.getId());
        List<List<InlineKeyboardButton>> usersTable = createUsersTable(users, chatId);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(usersTable);
        return markup;
    }

    public List<List<InlineKeyboardButton>> createUsersTable(List<User> users, Long chatId) {
        Set<Long> absentUsers;
        if (absentUsersMap.containsKey(chatId)) {
            absentUsers = absentUsersMap.get(chatId);
        } else {
            absentUsers = new LinkedHashSet<>();
            absentUsersMap.put(chatId, absentUsers);
        }
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (User u : users) {
            Room room = u.getRoom();
            String fullname = new StringBuilder(u.getLastName()).append(" ")
                    .append(u.getFirstName()).append(" ")
                    .append(u.getMiddleName()).append("  |  ")
                    .append(room != null ? room.getNumber() + "-xona" : "Xonaga biriktirilmagan").append("  |  ")
                    .append(absentUsers.contains(u.getId()) ? "❌" : "✅").toString();
            InlineKeyboardButton btn = new InlineKeyboardButton(fullname);
            btn.setCallbackData("toggle: " + u.getId());
            keyboard.add(List.of(btn));
        }
        InlineKeyboardButton saveBtn = new InlineKeyboardButton("Yakunlash \uD83D\uDD1A");
        saveBtn.setCallbackData("/end_duty");
        keyboard.add(List.of(saveBtn));
        return keyboard;
    }

    private SendMessage handleStart(Message message) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        KeyboardButton btn1 = new KeyboardButton("Davomat");
        KeyboardButton btn2 = new KeyboardButton("Navbatchilik");
        KeyboardButton btn3 = new KeyboardButton("Adminga aloqa");
        KeyboardRow r1 = new KeyboardRow(List.of(btn1, btn2));
        KeyboardRow r2 = new KeyboardRow(List.of(btn3));
        markup.setKeyboard(List.of(r1, r2));
        return createMessage(message.getChatId(), message.getMessageId(), markup, "Tizimga xush kelibsiz");
    }


    private void handleToken(String token) {

    }

    private boolean hasToken(String text) {
        return text != null && text.startsWith("/start ") && text.split(" ").length == 2;
    }

    public Boolean isRegisteredUser(String telegramUsername, Long telegramId) {
        return userRepository.existByTelegramUsernameAndTelegramId(telegramId, telegramUsername);
    }
}