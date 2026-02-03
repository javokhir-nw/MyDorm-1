package javier.com.mydorm1.service;

import javier.com.mydorm1.auth.dto.UserDto;
import javier.com.mydorm1.auth.model.User;
import javier.com.mydorm1.auth.repo.UserRepository;
import javier.com.mydorm1.dto.*;
import javier.com.mydorm1.model.Duty;
import javier.com.mydorm1.model.DutyItem;
import javier.com.mydorm1.repo.DutyItemRepository;
import javier.com.mydorm1.repo.DutyRepository;
import javier.com.mydorm1.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DutyService {
    private final DutyRepository dutyRepository;
    private final DutyItemRepository dutyItemRepository;
    private final UserRepository userRepository;
    private final Utils utils;

    public PageWrapper list(Pagination<Search> pagination) {
        Search search = pagination.getSearch();
        Date date = search.getDate();
        Long dormId = search.getDormId();
        Long floorId = search.getFloorId();
        Page<DutyDto> duties = dutyRepository.findAllByPagination(floorId,dormId,date, PageRequest.of(pagination.getPage(),pagination.getSize()));
        return PageWrapper.builder()
                .totalPages(duties.getTotalPages())
                .total(duties.getTotalElements())
                .list(duties.getContent())
                .build();
    }

    public DutyDto getById(Long id) {
        Duty duty = dutyRepository.findById(id).orElseThrow(() -> new RuntimeException("Bunday id li navbatchilik mavjud emas"));
        List<DutyItem> dutyItems = dutyItemRepository.getByDutyItem(id);
        DutyDto dto = new DutyDto(duty);
        List<DutyItemDto> dutyItemDtos = new ArrayList<>();
        for (DutyItem di : dutyItems){
            DutyItemDto childDto = new DutyItemDto(di);
            Set<Long> dutyUserIds = utils.extractIdsFromString(di.getDutyUserIds());
            childDto.setDutyUsers(userRepository.findAllByIds(dutyUserIds));
            dutyItemDtos.add(childDto);
        }
        dto.setDutyItemList(dutyItemDtos);
        return dto;
    }
}
