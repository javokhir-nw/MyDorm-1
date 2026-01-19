package javier.com.mydorm1.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import javier.com.mydorm1.auth.model.Status;
import javier.com.mydorm1.dto.*;
import javier.com.mydorm1.model.Room;
import javier.com.mydorm1.repo.FloorRepository;
import javier.com.mydorm1.repo.RoomRepository;
import javier.com.mydorm1.repo.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final FloorRepository floorRepository;
    private final RoomTypeRepository roomTypeRepository;

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
        }

        Integer capacity = dto.getCapacity();
        if (capacity != null){
            room.setCapacity(capacity);
        }

        String name = dto.getName();
        if (name != null) {
            room.setName(name);
        }

        Long roomTypeId = dto.getRoomTypeId();
        if (roomTypeId != null) {
            room.setRoomType(roomTypeRepository.findById(roomTypeId).orElseThrow(() -> new EntityNotFoundException("Room type with id " + roomTypeId + " not found")));
        } else {
            room.setRoomType(roomTypeRepository.findByCode("bedroom"));
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

    @Transactional
    public String deleteById(Long id) {
        roomRepository.changeStatusToDeleteById(id);
        return "SUCCESS_DELETED";
    }

    public Room findEntityById(Long roomId) {
        return roomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("Room is not found by id " + roomId));
    }

    public List<RoomResponseDto> getList(Long floorId) {
        return roomRepository.findByFloorId(floorId).stream().map(RoomResponseDto::new).toList();
    }
}
