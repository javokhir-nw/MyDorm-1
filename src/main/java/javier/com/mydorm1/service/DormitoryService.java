package javier.com.mydorm1.service;

import jakarta.persistence.EntityNotFoundException;
import javier.com.mydorm1.auth.model.Status;
import javier.com.mydorm1.auth.repo.UserRepository;
import javier.com.mydorm1.dto.*;
import javier.com.mydorm1.mapper.DormMapper;
import javier.com.mydorm1.model.Dormitory;
import javier.com.mydorm1.repo.DormitoryRepository;
import javier.com.mydorm1.repo.FloorRepository;
import javier.com.mydorm1.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DormitoryService {

    private final DormitoryRepository dormitoryRepository;
    private final FloorRepository floorRepository;
    private final DormMapper dormMapper;
    private final Utils utils;

    public String createOrUpdate(DormRequestDto dto) {
        Long id = dto.getId();
        Dormitory dormitory;
        if(id!=null){
            dormitory = dormitoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Dormitory not found"));
        } else {
            dormitory = new  Dormitory();
        }
        dormitory.setStatus(Status.ACTIVE);
        String name = dto.getName();
        if(name!=null){
            dormitory.setName(name);
        }
        dormitory.setOwner(utils.getCurrentUser());
        dormitoryRepository.save(dormitory);
        return "SUCCESS_SAVED";
    }

    @SneakyThrows
    public String update(DormRequestDto dto) {
        Long id = dto.getId();
        if(id == null){
            throw new BadRequestException("id is null");
        }
        return createOrUpdate(dto);
    }

    public DormResponseDto getById(Long id) {
        Dormitory dorm = dormitoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Dormitory not found"));
        DormResponseDto dto = new DormResponseDto(dorm);
        dto.setFloors(floorRepository.findAllByDormId(id).stream().map(FloorResponseDto::new).toList());
        return dto;
    }

    public PageWrapper getList(Pagination<Search> pagination) {
        Search search = pagination.getSearch();
        String value = search.getValue();
        utils.getCurrentUser();
        Page<Dormitory> page = dormitoryRepository.findAll(value, PageRequest.of(pagination.getPage(), pagination.getSize(), Sort.by(Sort.Direction.ASC, "id")));
        return PageWrapper.builder()
                .list(page.stream().map(dormMapper::toDormResponseDto).toList())
                .total(page.getTotalElements())
                .build();
    }

    public String delete(Long id) {
        dormitoryRepository.changeStatusToDeleted(id);
        return "SUCCESS_DELETED";
    }
}
