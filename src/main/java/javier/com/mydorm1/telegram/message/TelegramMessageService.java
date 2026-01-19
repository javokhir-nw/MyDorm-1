package javier.com.mydorm1.telegram.message;

import javier.com.mydorm1.auth.model.User;
import javier.com.mydorm1.auth.repo.UserRepository;
import javier.com.mydorm1.model.*;
import javier.com.mydorm1.repo.DutyItemRepository;
import javier.com.mydorm1.repo.DutyRepository;
import javier.com.mydorm1.util.Utils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TelegramMessageService {
    private final Utils utils;
    private final UserRepository userRepository;
    private final DutyItemRepository dutyItemRepository;
    private final DutyRepository dutyRepository;

    public String sendAttendanceReportToChat(Attendance attendance, Set<Long> absents) {
        StringBuilder sb = new StringBuilder();
        String date = utils.formatDateDDMMYYYY(attendance.getCreatedDate());
        Floor floor = attendance.getFloor();
        Long id = floor.getId();
        sb.append("🏢  ").append(floor.getName()).append(" davomat hisoboti (").append(date).append(" )\n\n");
        List<User> users = userRepository.findAllUsersFetchRoomByFloorId(id);
        int i = 1;
        for (User u : users) {
            String name = u.getLastName() + " " +
                    u.getFirstName();
            if (absents.contains(u.getId())){
                name = utils.createMarkdownMention(name,u.getTelegramId());
            }

            sb.append(i++).append(") ")
                    .append(name)
                    .append(" ").append(u.getRoom().getNumber()).append("-xona | ")
                    .append(
                            absents.contains(u.getId()) ? "❌" : "✅"
                    ).append("\n");
        }

        return sb.toString();
    }

    public String sendDutyReportToGroup(User user) {
        Floor floor = user.getFloor();
        String floorName = floor.getName();
        Long floorId = floor.getId();
        Duty todayDuty = dutyRepository.getTodayDuty(floorId, new Date());
        List<DutyItem> dutyItems = dutyItemRepository.getTodayDutyItemsByFloorId(floorId, new Date());

        StringBuilder sb = new StringBuilder();
        String date = utils.formatDateDDMMYYYY(todayDuty.getCreatedDate());
        sb.append("🏢  ").append(floorName).append(" navbatchilik hisoboti (").append(date).append(" )\n\n");
        List<User> users = userRepository.findAllUsersFetchRoomByFloorId(floorId);
        for (DutyItem di : dutyItems) {
            Room room = di.getRoom();
            sb.append("\uD83D\uDD18 ").append(room.getName()).append(") \n");
            int i = 1;
            Set<Long> usersOnDuty = utils.extractIdsFromString(di.getDutyUserIds());
            for (User u : users) {
                if (usersOnDuty.contains(u.getId())) {
                    String name = utils.createMarkdownMention(
                            u.getLastName() + " " +
                                    u.getFirstName() + " ",
                            u.getTelegramId()
                    );
                    sb.append(i++).append(") ")
                            .append(name)
                            .append(" ").append(u.getRoom().getNumber()).append("-xona")
                            .append("\n");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
