package javier.com.mydorm1.telegram.duty;

import jakarta.transaction.Transactional;
import javier.com.mydorm1.auth.model.User;
import javier.com.mydorm1.auth.repo.UserRepository;
import javier.com.mydorm1.model.Duty;
import javier.com.mydorm1.model.DutyItem;
import javier.com.mydorm1.model.Room;
import javier.com.mydorm1.model.RoomType;
import javier.com.mydorm1.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TelegramDutyService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final DutyRepository dutyRepository;
    private final DutyItemRepository dutyItemRepository;
    private final FloorRepository floorRepository;
    private Map<Long, Map<Long, Set<Long>>> dutyUsers = new LinkedHashMap<>();

    @Transactional
    public SendMessage createDuty(Long chatId, Integer messageId, User user) {
        Long floorId = user.getFloor().getId();
        InlineKeyboardMarkup markup = createRoomsList(floorId);
        return createMessage(chatId, markup, "Navbatchilik xonalari");
    }

    private SendMessage createMessage(Long chatId, InlineKeyboardMarkup markup, String text) {
        SendMessage edt = new SendMessage();
        edt.setChatId(chatId);
        edt.setText(text);
        edt.setReplyMarkup(markup);
        return edt;
    }

    private InlineKeyboardMarkup createRoomsList(Long floorId) {
        List<Room> rooms = roomRepository.findDutyRoomsByFloorId(floorId);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (Room r : rooms) {
            String number = r.getNumber();
            String roomTypeName = "";
            if (r.getRoomType() != null) {
                roomTypeName = r.getRoomType().getName();
            }
            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(number + "-xona (" + roomTypeName + ")" + "  [ + ]");
            btn.setCallbackData("duty:room: " + r.getId());
            keyboard.add(List.of(btn));
        }
        InlineKeyboardButton endBtn = new InlineKeyboardButton("Navbatchilikni tugatish");
        endBtn.setCallbackData("end_duty");
        keyboard.add(List.of(endBtn));
        markup.setKeyboard(keyboard);
        return markup;
    }

    public InlineKeyboardMarkup createUsersList(Long chatId, User user, Long roomId) {
        Long floorId = user.getFloor().getId();
        Map<Long, Set<Long>> roomUsers;
        if (dutyUsers.containsKey(chatId)) {
            roomUsers = dutyUsers.get(chatId);
        } else {
            roomUsers = new LinkedHashMap<>();
            dutyUsers.put(chatId, roomUsers);
        }
        Set<Long> usersOnDuty;
        if (roomUsers.containsKey(roomId)) {
            usersOnDuty = roomUsers.get(roomId);
        } else {
            DutyItem dutyItem = dutyItemRepository.getTodayDutyItemByRoomId(floorId, roomId, new Date());
            if (dutyItem != null && dutyItem.getDutyUserIds() != null && !dutyItem.getDutyUserIds().isEmpty()){
                usersOnDuty = Arrays.stream(dutyItem.getDutyUserIds().split(",")).map(Long::valueOf).collect(Collectors.toSet());
            } else {
                usersOnDuty = new LinkedHashSet<>();
            }
            roomUsers.put(roomId, usersOnDuty);
        }
        String idsString = attendanceRepository.getAbsentUsersString(floorId, new Date());
        List<Long> list;
        if (!idsString.isEmpty()) {
            list = Arrays.stream(idsString.split(","))
                    .map(Long::valueOf).toList();
        } else {
            list = new ArrayList<>();
        }
        List<User> users = userRepository.findAllPresentUsers(floorId);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (User u : users) {
            Room room = u.getRoom();
            String fullname = u.getLastName() + " " +
                    u.getFirstName() + " " +
                    u.getMiddleName() + (list.contains(u.getId()) ? " ❌" : "") + "  |  " +
                    (room != null ? room.getNumber() + "-xona" : "Xonaga biriktirilmagan") +
                    (usersOnDuty.contains(u.getId()) ? "   \uD83D\uDD35" : "");
            InlineKeyboardButton btn = new InlineKeyboardButton(fullname);
            btn.setCallbackData("attach:room: " + roomId + " :user_id: " + u.getId());
            keyboard.add(List.of(btn));
        }
        InlineKeyboardButton saveBtn = new InlineKeyboardButton("Yakunlash \uD83D\uDD1A");
        saveBtn.setCallbackData("end_duty_room: " + roomId);
        keyboard.add(List.of(saveBtn));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }

    public EditMessageText sendUsersListByRoomId(Long chatId, Integer messageId, User user, Long roomId) {
        InlineKeyboardMarkup markup = createUsersList(chatId, user, roomId);
        Room dutyRoom = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room is not exist by id on duty"));
        RoomType roomType = dutyRoom.getRoomType();
        String text = "Navbatchilik: " + dutyRoom.getNumber() + "-xona " + (roomType != null ? "(" + roomType.getName() + ")" : "");
        EditMessageText edt = new EditMessageText(text);
        edt.setMessageId(messageId);
        edt.setChatId(chatId);
        edt.setReplyMarkup(markup);
        return edt;
    }

    public void addOrRemoveUserFromDuty(Long chatId, Long roomId, Long userId) {
        Map<Long, Set<Long>> userRooms = dutyUsers.get(chatId);
        Set<Long> users;
        if (userRooms.containsKey(roomId)) {
            users = userRooms.get(roomId);
        } else {
            users = new LinkedHashSet<>();
            userRooms.put(roomId, users);
        }
        if (users.contains(userId)) {
            users.remove(userId);
        } else {
            users.add(userId);
        }
        userRooms.put(roomId, users);
        dutyUsers.put(chatId, userRooms);
    }

    public EditMessageText endDuty(Integer messageId, Long chatId, Long floorId, User user, Long roomId) {
        Map<Long, Set<Long>> roomsAndUsers = dutyUsers.get(chatId);
        Set<Long> userIds = roomsAndUsers.get(roomId);
        Duty duty = dutyRepository.getTodayDuty(floorId, new Date());
        if (duty == null) {
            duty = new Duty();
            duty.setFloor(floorRepository.findById(floorId).orElseThrow(() -> new RuntimeException("Duty saqlashda floorId bo'yicha floor topilmadi")));
            duty.setCreatedDate(new Date());
            duty.setCreator(user);
            duty = dutyRepository.save(duty);
        }
        DutyItem dutyItem = dutyItemRepository.getTodayDutyItemByRoomId(floorId, roomId, new Date());
        if (dutyItem == null) {
            dutyItem = new DutyItem();
            dutyItem.setCreatedDate(new Date());
            dutyItem.setDuty(duty);
        }
        dutyItem.setDutyUserIds(
                userIds.stream().map(String::valueOf).collect(Collectors.joining(","))
        );
        dutyItem.setRoom(roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Duty saqlashda roomId boyicha room topilmadi")));
        dutyItemRepository.save(dutyItem);
        dutyUsers.get(chatId).remove(roomId);
        return createUpdateMessage(chatId,messageId,createRoomsList(floorId),"Navbatchilik xonalari");
    }

    public EditMessageText createUpdateMessage(Long chatId,Integer messageId, InlineKeyboardMarkup markup, String text) {
        EditMessageText edt = new EditMessageText(text);
        edt.setMessageId(messageId);
        edt.setChatId(chatId);
        edt.setReplyMarkup(markup);
        return edt;
    }
}
