package javier.com.mydorm1.service;

import jakarta.persistence.EntityNotFoundException;
import javier.com.mydorm1.auth.model.Status;
import javier.com.mydorm1.dto.RoomRequestDto;
import javier.com.mydorm1.model.Room;
import javier.com.mydorm1.repo.FloorRepository;
import javier.com.mydorm1.repo.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final FloorRepository floorRepository;

    public String createOrUpdate(RoomRequestDto dto) {
        Long id = dto.getId();
        Room room;
        if (id != null) {
            room = roomRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Room with id " + id + " not found"));
        } else {
            room = new Room();
        }
        room.setStatus(Status.ACTIVE);

        String number = dto.getNumber();
        if (number != null) {
            room.setNumber(number);
            room.setName(number);
        }

        String name = dto.getName();
        if (name != null) {
            room.setName(name);
        }

        Long floorId = dto.getFloorId();
        if (floorId != null) {
            room.setFloor(floorRepository.findById(floorId).orElseThrow(() -> new EntityNotFoundException("Floor with id " + floorId + " not found")));
        }

        room.setIsRoom(dto.getIsRoom());
        roomRepository.save(room);
        return "SUCCESS_SAVED";
    }

    @SneakyThrows
    public String update(RoomRequestDto dto) {
        Long id = dto.getId();
        if (id == null) {
            throw new BadRequestException("Room id is null");
        }
        return createOrUpdate(dto);
    }

    public String deleteById(Long id) {
        roomRepository.changeStatusToDeleteById(id);
        return "SUCCESS_DELETED";
    }
}
