package javier.com.mydorm1.service;

import jakarta.persistence.EntityNotFoundException;
import javier.com.mydorm1.auth.model.Status;
import javier.com.mydorm1.dto.RoomTypeDto;
import javier.com.mydorm1.model.RoomType;
import javier.com.mydorm1.repo.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomTypeService {
    private final RoomTypeRepository roomTypeRepository;

    public String createOrUpdate(RoomTypeDto dto) {
        Long id = dto.getId();
        RoomType roomType;
        if (id != null) {
            roomType = roomTypeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("RoomType with id: " + id + " not found"));
        } else {
            roomType = new RoomType();
        }
        roomType.setStatus(Status.ACTIVE);
        roomType.setName(dto.getName());
        roomTypeRepository.save(roomType);
        return "SUCCESS_SAVED";
    }

    public String deleteById(Long id) {
        roomTypeRepository.changeStatusToDeleted(id);
        return "SUCCESS_DELETED";
    }

    public List<RoomTypeDto> getList() {
        return roomTypeRepository.findAll().stream().map(RoomTypeDto::new).toList();
    }
}
