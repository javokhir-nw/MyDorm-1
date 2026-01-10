package javier.com.mydorm1.telegram;

import jakarta.transaction.Transactional;
import javier.com.mydorm1.auth.model.User;
import javier.com.mydorm1.auth.repo.UserRepository;
import javier.com.mydorm1.model.Attendance;
import javier.com.mydorm1.model.Room;
import javier.com.mydorm1.repo.AttendanceRepository;
import javier.com.mydorm1.repo.DutyRepository;
import javier.com.mydorm1.model.Floor;
import javier.com.mydorm1.service.AttendanceService;
import javier.com.mydorm1.telegram.duty.TelegramDutyService;
import javier.com.mydorm1.util.Utils;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TelegramBotService {

    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final DutyRepository dutyRepository;
    private final TelegramDutyService telegramDutyService;
    private final Map<Long, Set<Long>> absentUsersMap = new LinkedHashMap<>();
    private final Utils utils;
    private final RegistrationService registrationService;
    private final AttendanceService attendanceService;

    @Transactional
    public EditMessageText handleCallBackQuery(CallbackQuery callbackQuery) {
        String url = callbackQuery.getData();

        Message message = callbackQuery.getMessage();
        Long chatId = message.getChatId();
        String userName = callbackQuery.getFrom().getUserName();
        Long userTelegramId = callbackQuery.getFrom().getId();

        User user = getUserByTelegramData(userName, userTelegramId);

        Integer messageId = message.getMessageId();
        if (url.startsWith("toggle: ")) {
            addOrRemoveUsersFromAbsentUsers(Long.valueOf(url.split(" ")[1]), chatId);
            return updateMessage(
                    chatId,
                    messageId,
                    startDavomat(user, chatId),
                    "Davomat ( " + utils.formatDateDDMMYYYY(new Date()) + " )"
            );
        } else if (url.equals("/end_attendance")) {
            finishAttendance(chatId, user);
            return updateMessage(
                    chatId,
                    messageId,
                    null,
                    "Davomat muvaffaqqiyatli saqlandi !"
            );
        } else if (url.startsWith("duty:room: ")) {
            return telegramDutyService.sendUsersListByRoomId(chatId, messageId, user, Long.valueOf(url.split(" ")[1]));
        } else if (url.startsWith("attach:room: ")) {
            Long roomId = Long.valueOf(url.split(" ")[1]);
            Long userId = Long.valueOf(url.split(" ")[3]);
            telegramDutyService.addOrRemoveUserFromDuty(chatId, roomId, userId);
            return telegramDutyService.sendUsersListByRoomId(chatId, messageId, user, roomId);
        } else if (url.startsWith("/end_duty: ")) {
            return telegramDutyService.endDuty(messageId, chatId, user.getFloor().getId(), user, Long.valueOf(url.split(" ")[1]));
        } else if (url.equals("to_main_menu")) {
            return telegramDutyService.createUpdateMessage(chatId, messageId, null, "Muvaffaqqiyatli saqlandi");
        } else if (url.contains("registration:")) {
            return registrationService.handleRegistrationCallBackQueries(callbackQuery);
        }
        return null;
    }

    public User getUserByTelegramData(String username, Long telegramId) {
        return userRepository.findByTelegramUsernameOrTelegramId(username, telegramId);
    }

    public void finishAttendance(Long chatId, User user) {
        Attendance attendance = attendanceRepository.getTodayAttendance(user.getFloor().getId(), new Date()).orElse(new Attendance());
        Set<Long> absents = absentUsersMap.get(chatId);
        String result = absents.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        attendance.setAbsentUserIds(result);
        attendance.setCreatedDate(new Date());
        attendance.setFloor(user.getFloor());
        attendanceRepository.save(attendance);
        absentUsersMap.remove(chatId);
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
        Long chatId = message.getChatId();
        Long userTelegramId = message.getFrom().getId();
        String userTelegramUsername = message.getFrom().getUserName();
        if (hasToken(text)) {
            String token = text.split(" ")[1];
            return registrationService.registerUser(token, chatId, userTelegramId, userTelegramUsername, message.getMessageId());
        } else {
            return handleMessage(message);
        }
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
        String text = message.getText() == null ? "" : message.getText();
        String userName = message.getFrom().getUserName();
        Long userId = message.getFrom().getId();
        User user = getUserByTelegramData(userName, userId);
        Long chatId = message.getChatId();
        Integer messageId = message.getMessageId();
        switch (text) {
            case "/start" -> {
                return handleStart(message);
            }
            case "Davomat" -> {
                return createMessage(chatId, messageId, startDavomat(user, chatId), "Davomat ( " + utils.formatDateDDMMYYYY(new Date()) + " )");
            }
            case "Navbatchilik" -> {
                return telegramDutyService.createDuty(chatId, messageId, user);
            }
            case "Adminga aloqa" -> {
                return createMessage(chatId, messageId, null, "Admin: @javokhir_nw ");
            }
            default -> {
                return registrationService.handleRegisterAnswers(message);
            }
        }
    }


    public InlineKeyboardMarkup startDavomat(User user, Long chatId) {
        Floor floor = user.getFloor();
        Long floorId = floor.getId();
        List<User> users = userRepository.findAllUsersFetchRoomByFloorId(floorId);
        List<List<InlineKeyboardButton>> usersTable = createUsersTable(users, chatId, floorId);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(usersTable);
        return markup;
    }

    public List<List<InlineKeyboardButton>> createUsersTable(List<User> users, Long chatId, Long floorId) {
        Set<Long> absentUsers;
        if (absentUsersMap.containsKey(chatId)) {
            absentUsers = absentUsersMap.get(chatId);
        } else {
            String absentUsersString = attendanceRepository.getAbsentUsersString(floorId, new Date());
            if (absentUsersString != null && !absentUsersString.isEmpty()) {
                absentUsers = Arrays.stream(absentUsersString.split(",")).map(Long::valueOf).collect(Collectors.toSet());
            } else {
                absentUsers = new LinkedHashSet<>();
            }
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
        saveBtn.setCallbackData("/end_attendance");
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

    private boolean hasToken(String text) {
        return text != null && text.startsWith("/start ") && text.split(" ").length == 2;
    }

    public Boolean isRegisteredUser(String telegramUsername, Long telegramId) {
        return userRepository.existByTelegramUsernameAndTelegramId(telegramId, telegramUsername);
    }
}