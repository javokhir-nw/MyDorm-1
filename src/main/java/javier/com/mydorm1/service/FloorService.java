package javier.com.mydorm1.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import javier.com.mydorm1.auth.model.Status;
import javier.com.mydorm1.auth.model.User;
import javier.com.mydorm1.auth.repo.UserRepository;
import javier.com.mydorm1.dto.*;
import javier.com.mydorm1.model.Floor;
import javier.com.mydorm1.repo.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FloorService {

    private final FloorRepository floorRepository;
    private final UserRepository userRepository;
    private final DormitoryRepository dormitoryRepository;
    private final RoomRepository roomRepository;

    public String createOrUpdate(FloorRequestDto dto) {
        Long id = dto.getId();
        Floor fl;
        if (id != null) {
            fl = floorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Floor is not found"));
        } else {
            fl = new Floor();
        }
        String name = dto.getName();
        if (name != null) {
            fl.setName(name);
        }

        String randomString = dto.getRandString();
        if(randomString != null && !randomString.isEmpty()){
            fl.setFloorTelegramIdentity(randomString);
        }
        Long dormId = dto.getDormId();
        if (dormId != null) {
            fl.setDormitory(dormitoryRepository.findById(dormId).orElseThrow(() -> new EntityNotFoundException("Dormitory is not found")));
        }
        floorRepository.save(fl);
        return "SUCCESS_SAVED";
    }

    @SneakyThrows
    public String update(FloorRequestDto dto) {
        Long id = dto.getId();
        if (id == null){
            throw new BadRequestException("Floor id is null");
        }
        return createOrUpdate(dto);
    }

    public PageWrapper getList(Long dormId, Pagination<Search> pagination) {
        Search search = pagination.getSearch();
        String value = search.getValue();
        Map<Long, List<User>> allCaptains = userRepository.findAllCaptains().stream()
                .filter(u -> u.getFloor() != null)
                .collect(Collectors.groupingBy(u -> u.getFloor().getId()));
        Page<Floor> floors = floorRepository.findAllByDormId(dormId,value, PageRequest.of(pagination.getPage(),pagination.getSize()));
        List<FloorResponseDto> list = floors.stream().map(f -> new FloorResponseDto(f,allCaptains.get(f.getId()))).toList();
        return PageWrapper.builder().total(floors.getTotalElements())
                .totalPages(floors.getTotalPages()).list(list).build();
    }

    public FloorResponseDto getById(Long id) {
        Floor floor = getEntityById(id);
        FloorResponseDto dto = new FloorResponseDto(floor);
        List<RoomResponseDto> rooms = roomRepository.findByFloorId(id).stream().map(RoomResponseDto::new).toList();
        dto.setRooms(rooms);
        return dto;
    }

    public Floor getEntityById(Long id){
        return floorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Floor is not found"));
    }

    @Transactional
    public String deleteById(Long id) {
        floorRepository.updateFloorStatus(id,Status.DELETED);
        return "SUCCESS_DELETED";
    }
}
